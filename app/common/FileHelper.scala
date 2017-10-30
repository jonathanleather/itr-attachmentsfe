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

package common

import models.fileUpload.EnvelopeFile

object FileHelper extends FileHelper {

}

trait FileHelper {

  final val PDF = "application/pdf"
  final val XLS = "application/vnd.ms-excel"
  final val XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
  final val JPG = "image/jpeg"
  final val DEFAULT = "application/octet-stream"
  final val CSV = "text/csv"

  def getMimeType(filename: String): String = {

    filename.split('.').drop(1).lastOption match {
      case Some(ext) => ext.toLowerCase() match {
        case "pdf" => PDF
        case "jpg" => JPG
        case "jpeg" => JPG
        case "xls" => XLS
        case "xlsx" => XLSX
        case "csv" => CSV
        case _ => DEFAULT
      }
      case None => DEFAULT
    }
  }

  def isAllowableFileType(fileName: String): Boolean = {
    fileName.matches("""^.*\.(jpg|JPG|jpeg|JPEG|xls|XLS|pdf|PDF|xlsx|XLSX|csv|CSV)$""")
  }

  def withinEnvelopeMaximumSize(existingFiles: Seq[EnvelopeFile], additionalFileSize: Int): Boolean = {
    // TODO: API is not returning file length in all states. If fixed renable commented line and make EnvelopeFile non optional for length
    // //val x = (existingFiles.foldLeft(0)(_ + _.length) + additionalFileSize) <= Constants.envelopeLimit
    (existingFiles.foldLeft(0)(_ + _.length.fold(0)(_.self)) + additionalFileSize) <= Constants.envelopeLimit
  }

}
