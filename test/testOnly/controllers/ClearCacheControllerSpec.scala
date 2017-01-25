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
import connectors.{S4LConnector, EnrolmentConnector}
import helpers.ControllerSpec
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._
import testOnly.controllers.ClearCacheController
import uk.gov.hmrc.play.http.HttpResponse

class ClearCacheControllerSpec extends ControllerSpec{

  object TestClearCacheController extends ClearCacheController{
    override lazy val enrolmentConnector = mockEnrolmentConnector
    override lazy val applicationConfig= MockConfig
    override protected def authConnector = MockAuthConnector
    override lazy val s4LConnector = mockS4lConnector
  }


  "ClearCacheControllerSpec" should {
    "use the save for later connector" in {
      ClearCacheController.s4LConnector shouldBe S4LConnector
    }

    "use the correct enrollment connector" in {
      ClearCacheController.enrolmentConnector shouldBe EnrolmentConnector
    }

    "use the correct config" in {
      ClearCacheController.applicationConfig shouldBe FrontendAppConfig
    }

    "use the correct auth connector" in {
      ClearCacheController.authConnector shouldBe FrontendAuthConnector
    }
  }





  "Issuing a request to the ClearCacheController clearCache method when authenticated and enrolled" should {
    "return a 200 Ok if cache is successfully cleared" in {
      mockEnrolledRequest()
      when(TestClearCacheController.s4LConnector.clearCache()(Matchers.any(), Matchers.any())).thenReturn(HttpResponse(NO_CONTENT))
      showWithSessionAndAuth(TestClearCacheController.clearCache())(
        result => {
          status(result) shouldBe OK
        }
      )
    }

    "return a BADRequest if cache is unsuccessfully cleared" in {
      mockEnrolledRequest()
      when(TestClearCacheController.s4LConnector.clearCache()(Matchers.any(), Matchers.any())).thenReturn(HttpResponse(INTERNAL_SERVER_ERROR))
      showWithSessionAndAuth(TestClearCacheController.clearCache())(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Issuing a request to the ClearCacheController clearCache method when authenticated but not enrolled" should {
    "redirect to the subscription service" in {
      mockNotEnrolledRequest()
      showWithSessionAndAuth(TestClearCacheController.clearCache())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(TestClearCacheController.applicationConfig.subscriptionUrl)
        }
      )
    }
  }

  "Issuing a request to the ClearCacheController clearCache method when not authenticated" should {
    "redirect to the gg login" in {
      mockNotEnrolledRequest()
      showWithSessionWithoutAuth(TestClearCacheController.clearCache())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${TestClearCacheController.applicationConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(TestClearCacheController.applicationConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Issuing a request to the ClearCacheController clearCache method with no session" should {
    "redirect to the gg login" in {
      mockNotEnrolledRequest()
      showWithoutSession(TestClearCacheController.clearCache())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${TestClearCacheController.applicationConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(TestClearCacheController.applicationConfig.introductionUrl, "UTF-8")
          }&origin=investment-tax-relief-attachments-frontend&accountType=organisation")
        }
      )
    }
  }

  "Issuing a Timed out request to the ClearCacheController" should {
    "redirect to the Timeout page" in {
      mockNotEnrolledRequest()
      showWithTimeout(TestClearCacheController.clearCache())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }
}
