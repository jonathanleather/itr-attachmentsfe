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

package controllers

import java.net.URLEncoder

import akka.util.ByteString
import auth.{MockAuthConnector, MockConfig}
import common.{ControllerSpec, KeystoreKeys}
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, KeystoreConnector}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.api.test.Helpers._
import services.FileUploadService
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future

class FileUploadControllerSpec extends ControllerSpec {

  val envelopeID = "00000000-0000-0000-0000-000000000000"
  val fileName = "test.pdf"
  val tempFile = ByteString("1")
  val testUrl = "http://"

  lazy val multipartFormData = {
    val part = FilePart(key = "supporting-docs", filename = fileName, contentType = Some(".pdf"), ref = tempFile)
    MultipartFormData(
      dataParts = Map("envelope-id" -> Seq(envelopeID)),
      files = Seq(part),
      badParts = Seq()
    )
  }

  lazy val multipartFormDataNoFile = {
    val part = FilePart(key = "invalid", filename = fileName, contentType = Some(".pdf"), ref = tempFile)
    MultipartFormData(
      dataParts = Map("envelope-id" -> Seq(envelopeID)),
      files = Seq(part),
      badParts = Seq()
    )
  }

  object TestController extends FileUploadController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
    override lazy val keyStoreConnector = mockKeyStoreConnector
    override lazy val fileUploadService = mockFileUploadService

  }

  def setupMocks(): Unit = {
    when(mockFileUploadService.getEnvelopeID(Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
      .thenReturn(envelopeID)
    when(mockFileUploadService.getEnvelopeFiles(Matchers.eq(envelopeID))(Matchers.any(), Matchers.any()))
      .thenReturn(files)
  }

  "FileUploadController" should {
    "use the correct auth connector" in {
      FileUploadController.authConnector shouldBe FrontendAuthConnector
    }
    "use the correct enrolment connector" in {
      FileUploadController.enrolmentConnector shouldBe EnrolmentConnector
    }
    "use the correct file upload service" in {
      FileUploadController.fileUploadService shouldBe FileUploadService
    }
    "use the correct file keystore connector" in {
      FileUploadController.keyStoreConnector shouldBe KeystoreConnector
    }

  }

  "Sending a GET request to FileUploadController when authenticated and NOT enrolled" should {
    "return a 200 when something is fetched from keystore" in {
      mockNotEnrolledRequest()
      showWithSessionAndAuth(TestController.show(Some("continue"), Some("back")))(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(FrontendAppConfig.subscriptionUrl)
        }
      )
    }
  }

  "Sending an Unauthenticated request with a session to FileUploadController" should {
    "return a 302 and redirect to GG login" in {
      showWithSessionWithoutAuth(TestController.show(Some("continue"), Some("back")))(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a request with no session to FileUploadController" should {
    "return a 302 and redirect to GG login" in {
      showWithoutSession(TestController.show(Some("continue"), Some("back")))(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a timed-out request to FileUploadController" should {
    "return a 302 and redirect to the timeout page" in {
      showWithTimeout(TestController.show(Some("continue"), Some("back")))(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }

  // fix
//  "Posting to the FileUploadController when authenticated and enrolled" should {
//    "redirect to 'CheckYourAnswersPage' page" in
//      mockEnrolledRequest()
//      setupMocks()
//      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backUrl))(Matchers.any(), Matchers.any()))
//        .thenReturn(Option(testUrl))
//      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.continueUrl))(Matchers.any(), Matchers.any()))
//        .thenReturn(Option(testUrl))
//      submitWithSessionAndAuth(TestController.submit)(
//        result => {
//          status(result) shouldBe SEE_OTHER
//          redirectLocation(result) shouldBe Some("/investment-tax-relief/check-your-answers")
//        }
//      )
//    }


  "Posting to the FileUploadController when not authenticated" should {
    "redirect to the GG login page when having a session but not authenticated" in {
      submitWithSessionWithoutAuth(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }

    "redirect to the GG login page with no session" in {
      submitWithoutSession(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a attachments to the FileUploadController when a timeout has occured" should {
    "redirect to the Timeout page when session has timed out" in {
      submitWithTimeout(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }

  "Sending a attachments to the FileUploadController when not enrolled" should {
    "redirect to the Subscription Service" in {
      mockNotEnrolledRequest()
      submitWithSessionAndAuth(TestController.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(FrontendAppConfig.subscriptionUrl)
        }
      )
    }
  }

  "Sending a GET request to FileUploadController when continueUrl is none" should {
    "return a 400 when no matching continue Url is returned from keystore" in {
      mockEnrolledRequest()
      setupMocks()
      when(mockKeyStoreConnector.fetchAndGetFormData(Matchers.eq(KeystoreKeys.continueUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(None)
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(testUrl)))
      showWithSessionAndAuth(TestController.show(None, Some("back")))(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending a GET request to FileUploadController when backurl is none" should {
    "return a 400 when no matching back Url is returned from keystore" in {
      mockEnrolledRequest()
      setupMocks()
      when(mockKeyStoreConnector.fetchAndGetFormData(Matchers.eq(KeystoreKeys.backUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(None)
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.continueUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(testUrl)))
      showWithSessionAndAuth(TestController.show(Some("continue"),None))(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending a GET request to FileUploadController when backurl is none" should {
    "return a 200 when a matching back Url is returned from keystore" in {
      mockEnrolledRequest()
      setupMocks()
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(testUrl)))
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.continueUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(testUrl)))
      showWithSessionAndAuth(TestController.show(None, Some("back")))(
        result => {
          status(result) shouldBe OK
        }
      )
    }
  }

  "Sending a GET request to FileUploadController when continueUrl is none" should {
    "return a 200 when a matching continue Url is returned from keystore" in {
      mockEnrolledRequest()
      setupMocks()
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(Option(testUrl))
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.continueUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(Option(testUrl))
      showWithSessionAndAuth(TestController.show(Some("continue"), None))(
        result => {
          status(result) shouldBe OK
        }
      )
    }
  }

  "Sending a GET request to FileUploadController when both continueUrl and back url are none" should {
    "return a 200 when a matching continue Url and backUrl are returned from keystore" in {
      mockEnrolledRequest()
      setupMocks()
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(Option(testUrl))
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.continueUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(Option(testUrl))
      showWithSessionAndAuth(TestController.show(None, None))(
        result => {
          status(result) shouldBe OK
        }
      )
    }
  }

  "Sending a GET request to FileUploadController when both continueUrl and back url are passed as empty strings" should {
    "return a 400 when no matching continue Url and backUrls are returned from keystore" in {
      mockEnrolledRequest()
      setupMocks()
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(None)
      when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.continueUrl))(Matchers.any(), Matchers.any()))
        .thenReturn(None)
      showWithSessionAndAuth(TestController.show(Some(""), Some("")))(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Uploading a file to the FileUploadController" when {
    "the file limit has not been succeeded, the file passes validation and uploads successfully" should {
      "redirect to the file upload page" in {
        when(mockFileUploadService.belowFileNumberLimit(Matchers.eq(envelopeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(true))
        when(mockFileUploadService.validateFile(Matchers.eq(envelopeID), Matchers.eq(fileName), Matchers.eq(tempFile.length))
        (Matchers.any(), Matchers.any())).thenReturn(Future.successful(Seq(true, true, true)))
        when(mockFileUploadService.uploadFile(Matchers.eq(tempFile), Matchers.eq(fileName), Matchers.eq(envelopeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(HttpResponse(OK)))
        submitWithMultipartFormData(TestController.upload, multipartFormData)(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(routes.FileUploadController.show().url)
          }
        )
      }
    }

    "the file limit has not been succeeded and the file doesn't pass validation" should {
      "return a BAD_REQUEST" in {
        mockEnrolledRequest()
        setupMocks()
        when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backUrl))(Matchers.any(), Matchers.any()))
          .thenReturn(Option(testUrl))
        when(mockKeyStoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.continueUrl))(Matchers.any(), Matchers.any()))
          .thenReturn(Option(testUrl))
        when(mockFileUploadService.belowFileNumberLimit(Matchers.eq(envelopeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(true))
        when(mockFileUploadService.validateFile(Matchers.eq(envelopeID), Matchers.eq(fileName), Matchers.eq(tempFile.length))
        (Matchers.any(), Matchers.any())).thenReturn(Future.successful(Seq(false, false, true)))
        submitWithMultipartFormData(TestController.upload, multipartFormData)(
          result => {
            status(result) shouldBe BAD_REQUEST
          }
        )
      }
    }

    "the file limit has not been succeeded, the file passes validation and doesn't upload successfully" should {
      "return an INTERNAL_SERVER_ERROR" in {
        when(mockFileUploadService.belowFileNumberLimit(Matchers.eq(envelopeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(true))
        when(mockFileUploadService.validateFile(Matchers.eq(envelopeID), Matchers.eq(fileName), Matchers.eq(tempFile.length))
        (Matchers.any(), Matchers.any())).thenReturn(Future.successful(Seq(true, true, true)))
        when(mockFileUploadService.uploadFile(Matchers.eq(tempFile), Matchers.eq(fileName), Matchers.eq(envelopeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR)))
        submitWithMultipartFormData(TestController.upload, multipartFormData)(
          result => {
            status(result) shouldBe INTERNAL_SERVER_ERROR
          }
        )
      }
    }

    "the file limit has been succeeded" should {
      "redirect to the file upload page" in {
        when(mockFileUploadService.belowFileNumberLimit(Matchers.eq(envelopeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(false))
        submitWithMultipartFormData(TestController.upload, multipartFormData)(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(routes.FileUploadController.show().url)
          }
        )
      }
    }

    "no file is added to the form body under supporting-docs" should {
      "redirect to the file upload page" in {
        when(mockFileUploadService.belowFileNumberLimit(Matchers.eq(envelopeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(true))
        submitWithMultipartFormData(TestController.upload, multipartFormDataNoFile)(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(routes.FileUploadController.show().url)
          }
        )
      }
    }

  }
}
