/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import akka.util.ByteString
import auth.TAVCUser
import common.{Constants, FileHelper, KeystoreKeys}
import config.FrontendAppConfig
import connectors.{AttachmentsConnector, FileUploadConnector, KeystoreConnector, S4LConnector}
import models.fileUpload.{Envelope, EnvelopeFile, MetadataModel}
import play.api.Logger
import play.mvc.Http.Status._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.binders.ContinueUrl

import scala.concurrent.{ExecutionContext, Future}

object FileUploadService extends FileUploadService {
  override lazy val fileUploadConnector = FileUploadConnector
  override lazy val s4lConnector = S4LConnector
  override lazy val attachmentsConnector = AttachmentsConnector
  override def baseUrl: String = FrontendAppConfig.baseUrl
}

trait FileUploadService {

  final val PDF = "application/pdf"
  final val XML = "application/xml"

  val fileUploadConnector: FileUploadConnector
  val s4lConnector: S4LConnector
  val attachmentsConnector: AttachmentsConnector
  val withinFileSize = true
  val fileIsUnique = true
  val fileWithinEnvelopeLimit = true
  def baseUrl: String

  def storeRedirectParameterIfValid(parameter: String, keyStoreKey:String, keyStoreConnector:KeystoreConnector)
                                   (implicit hc: HeaderCarrier, ex: ExecutionContext): Boolean = {
    if (parameter.length > 0) {
      val validatedBackUrl = ContinueUrl(parameter)
      if (validatedBackUrl.url.startsWith(baseUrl)) {
        keyStoreConnector.saveFormData(keyStoreKey, validatedBackUrl.url).map {
          result => result
        }
        true
      }
      else false
    }
    else true
  }

  def validateFile(envelopeID: String, fileName: String, fileSize: Int)
                  (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Seq[Boolean]] = {

    val fileSizeWithinLimit: Int => Boolean = length => length <= Constants.fileSizeLimit

    def fileNameUnique(files: Seq[EnvelopeFile]): Boolean = {
      def compareFilenames(files: Seq[EnvelopeFile], index: Int = 0): Boolean = {
        if (files(index).name.equalsIgnoreCase(fileName)) false
        else if (index < files.length - 1) compareFilenames(files, index + 1)
        else true
      }

      compareFilenames(files)
    }

    getEnvelopeFiles(envelopeID).map {
      case files if files.nonEmpty =>
        if (fileNameUnique(files)) {
          Seq(fileIsUnique, fileSizeWithinLimit(fileSize), FileHelper.isAllowableFileType(fileName),
            FileHelper.withinEnvelopeMaximumSize(files, fileSize))
        }
        else {
          Seq(!fileIsUnique, fileSizeWithinLimit(fileSize), FileHelper.isAllowableFileType(fileName),
            FileHelper.withinEnvelopeMaximumSize(files, fileSize))
        }
      case _ => Seq(fileIsUnique, fileSizeWithinLimit(fileSize), FileHelper.isAllowableFileType(fileName), fileWithinEnvelopeLimit)
    }

  }

  def getEnvelopeID(createNewID: Boolean = true)(implicit hc: HeaderCarrier, ex: ExecutionContext, user: TAVCUser): Future[String] = {
    s4lConnector.fetchAndGetFormData[String](KeystoreKeys.envelopeID).flatMap {
      case Some(envelopeID) if envelopeID.nonEmpty =>
        // make sure this id is not orphaned
        checkEnvelopeStatus(envelopeID).flatMap {
          case Some(envelope) => Future(envelope.id)
          case _ if createNewID => createEnvelopeId
          case _ => Future("")
        }
      case _ if createNewID => createEnvelopeId
      case _ => Future.successful("")
    }
  }

  private def createEnvelopeId()(implicit hc: HeaderCarrier, ex: ExecutionContext, user: TAVCUser): Future[String] = {

    attachmentsConnector.createEnvelope().map {
      result =>
        val envelopeID = result.json.\("envelopeID").as[String]
        s4lConnector.saveFormData(KeystoreKeys.envelopeID, envelopeID)
        envelopeID
    }
  }

  def checkEnvelopeStatus(envelopeID: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Option[Envelope]] = {
    attachmentsConnector.getEnvelopeStatus(envelopeID).map {
      result => result.status match {
        case OK => result.json.asOpt[Envelope]
        case _ =>
          Logger.warn(s"[FileUploadConnector][checkEnvelopeStatus] Error ${result.status} received.")
          None
      }
    }
  }

  def uploadFile(file: ByteString, fileName: String, envelopeID: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    for {
      fileID <- generateFileID(envelopeID)
      result <- fileUploadConnector.addFileContent(envelopeID, fileID, fileName, file, FileHelper.getMimeType(fileName))
    } yield result.status match {
      case OK => HttpResponse(result.status)
      case _ =>
        Logger.warn(s"[FileUploadConnector][uploadFile] Error ${result.status} received.")
        HttpResponse(result.status)
    }
  }

  def getEnvelopeFiles(envelopeID: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Seq[EnvelopeFile]] = {
    checkEnvelopeStatus(envelopeID).map {
      case Some(envelope) => envelope.files.getOrElse(Seq())
      case _ => Seq()
    }
  }

  def closeEnvelope(tavcRef: String, envelopeId: String, id: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    addMetadataFile(envelopeId, tavcRef).flatMap[HttpResponse] {
      case true => attachmentsConnector.closeEnvelope(envelopeId).map {
        result => result.status match {
          case OK =>
            s4lConnector.saveFormData(id, KeystoreKeys.envelopeID, "")
            result
          case _ => Logger.warn(s"[FileUploadConnector][closeEnvelope] Error closing envelope. Status ${result.status} received.")
            s4lConnector.saveFormData(id, KeystoreKeys.envelopeID, "")
            result
        }
      }
      case false => Logger.warn(s"[FileUploadConnector][closeEnvelope] Error creating metadata.")
        s4lConnector.saveFormData(id, KeystoreKeys.envelopeID, "")
        Future.successful(HttpResponse(INTERNAL_SERVER_ERROR))
    }.recover {
      case e: Exception => Logger.warn(s"[FileUploadConnector][closeEnvelope] Error response status ${e.getMessage} received.")
        s4lConnector.saveFormData(id, KeystoreKeys.envelopeID, "")
        HttpResponse(INTERNAL_SERVER_ERROR)
    }
  }

  def deleteFile(fileID: String)(implicit hc: HeaderCarrier, ex: ExecutionContext, user: TAVCUser): Future[HttpResponse] =
    getEnvelopeID(createNewID = false).flatMap {
      case envelopeID if envelopeID.nonEmpty =>
        attachmentsConnector.deleteFile(envelopeID, fileID).map {
          result => result.status match {
            case OK => result
            case _ => Logger.warn(s"[FileUploadConnector][deleteFile] Error deleting file. Status ${result.status} received.")
              result
          }
        }
      case _ => Future.successful(HttpResponse(INTERNAL_SERVER_ERROR))
    }

  private def generateFileID(envelopeID: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Int] = {
    attachmentsConnector.getEnvelopeStatus(envelopeID).map {
      result =>
        val envelope = result.json.as[Envelope]
        if (envelope.files.isDefined)
          if (envelope.files.get.nonEmpty) envelope.files.get.last.id.toInt + 1
          else 1
        else 1
    }
  }

  private def addMetadataFile(envelopeID: String, tavcRef: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Boolean] = {
    generateFileID(envelopeID).flatMap {
      fileID =>
        fileUploadConnector.addFileContent(envelopeID, fileID, s"$envelopeID-metadata.xml", MetadataModel(envelopeID, tavcRef).getControlFile, XML).map {
          result => result.status match {
            case OK => true
            case _ => Logger.warn(s"[FileUploadConnector][addMetadataFile] Error creating metadata. Response ${result.status} received.")
              false
          }
        }
    }
  }

}
