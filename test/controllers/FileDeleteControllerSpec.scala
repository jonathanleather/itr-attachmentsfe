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

import auth.{MockConfig, MockAuthConnector}
import config.{FrontendAuthConnector, FrontendAppConfig}
import connectors.{EnrolmentConnector}
import helpers.ControllerSpec
import org.mockito.Matchers
import org.mockito.Mockito._
import services.FileUploadService
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.HttpResponse

class FileDeleteControllerSpec extends ControllerSpec{

  object TestFileDeleteController extends FileDeleteController{
    override lazy val fileUploadService = mockFileUploadService
    override lazy val enrolmentConnector = mockEnrolmentConnector
    override lazy val applicationConfig= MockConfig
    override protected def authConnector = MockAuthConnector
  }


  "FileDeleteControllerSpec" should {
    "use the correct file upload service" in {
      FileDeleteController.fileUploadService shouldBe FileUploadService
    }

    "use the correct enrollment connector" in {
      FileDeleteController.enrolmentConnector shouldBe EnrolmentConnector
    }

    "use the correct config" in {
      FileDeleteController.applicationConfig shouldBe FrontendAppConfig
    }

    "use the correct auth connector" in {
      FileDeleteController.authConnector shouldBe FrontendAuthConnector
    }
  }



  def setupShowMocks(){
    when(TestFileDeleteController.fileUploadService.getEnvelopeID(Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(envelopeId)
    when(TestFileDeleteController.fileUploadService.getEnvelopeFiles(Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(maxFiles)
  }
  def setupSubmitMocks(httpResponse: HttpResponse){
    when(TestFileDeleteController.fileUploadService.deleteFile(Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any())).
      thenReturn(httpResponse)

  }

  "Issuing a GET request to the FileDeleteController when authenticated and enrolled" should {
    "return a 200 Ok" in {
      mockEnrolledRequest()
      setupShowMocks()
      showWithSessionAndAuth(TestFileDeleteController.show(fileId))(
        result => {
          status(result) shouldBe OK
        }
      )
    }
  }

  "Issuing a GET request to the FileDeleteController when authenticated and not enrolled" should {
    "redirect to the subscription service" in {
      mockNotEnrolledRequest()
      showWithSessionAndAuth(TestFileDeleteController.show(fileId))(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(TestFileDeleteController.applicationConfig.subscriptionUrl)
        }
      )
    }
  }

  "Issuing a GET request to the FileDeleteController neither authenticated or enrolled" should {
    "redirect to the gg login" in {
      mockNotEnrolledRequest()
      showWithSessionWithoutAuth(TestFileDeleteController.show(fileId))(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${TestFileDeleteController.applicationConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(TestFileDeleteController.applicationConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Issuing a GET request to the FileDeleteController with no session" should {
    "redirect to the gg login" in {
      mockNotEnrolledRequest()
      showWithoutSession(TestFileDeleteController.show(fileId))(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${TestFileDeleteController.applicationConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(TestFileDeleteController.applicationConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Issuing a Timed out GET request to the FileDeleteController" should {
    "redirect to the Timeout page" in {
      mockNotEnrolledRequest()
      showWithTimeout(TestFileDeleteController.show(fileId))(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }




  "Issuing a POST request to the FileDeleteController when authenticated and enrolled" should {
    "redirect to the file upload page when the file is successfully deleted" in {
      mockEnrolledRequest()
      setupSubmitMocks(HttpResponse(OK))
      val formInput = "file-id" -> fileId
      submitWithSessionAndAuth(TestFileDeleteController.submit(),formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.FileUploadController.show().url)
        }
      )
    }
  }

  "Issuing a POST request to the FileDeleteController when authenticated and enrolled" should {
    "return an INTERNAL_SERVER_ERROR if the file cannot be deleted" in {
      mockEnrolledRequest()
      setupSubmitMocks(HttpResponse(INTERNAL_SERVER_ERROR))
      val formInput = "file-id" -> fileId
      submitWithSessionAndAuth(TestFileDeleteController.submit(), formInput)(
        result => {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      )
    }
  }

  "Issuing a POST request to the FileDeleteController when authenticated and enrolled" should {
    "return an INTERNAL_SERVER_ERROR when a form with errors is posted" in {
      mockEnrolledRequest()
      val formInput = "file-id" -> ""
      submitWithSessionAndAuth(TestFileDeleteController.submit(),formInput)(
        result => {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      )
    }
  }


  "Issuing a POST request to the FileDeleteController when authenticated and not enrolled" should {
    "redirect to the subscription service" in {
      mockNotEnrolledRequest()
      submitWithSessionAndAuth(TestFileDeleteController.submit())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(TestFileDeleteController.applicationConfig.subscriptionUrl)
        }
      )
    }
  }

  "Issuing a POST request to the FileDeleteController neither authenticated or enrolled" should {
    "redirect to the gg login screen" in {
      mockNotEnrolledRequest()
      submitWithSessionWithoutAuth(TestFileDeleteController.submit())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${TestFileDeleteController.applicationConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(TestFileDeleteController.applicationConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Issuing a POST request to the FileDeleteController with no session" should {
    "redirect to the gg login screen" in {
      mockNotEnrolledRequest()
      submitWithoutSession(TestFileDeleteController.submit())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${TestFileDeleteController.applicationConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(TestFileDeleteController.applicationConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Issuing a Timed out POST request to the FileDeleteController" should {
    "redirect to the Timeout Page" in {
      mockNotEnrolledRequest()
      submitWithTimeout(TestFileDeleteController.submit())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }
}
