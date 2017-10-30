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

import auth.MockAuthConnector
import common.ControllerSpec
import controllers.internal.InternalController
import play.api.libs.json.Json
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.EnrolmentConnector
import services.FileUploadService
import uk.gov.hmrc.http.cache.client.CacheMap
import org.mockito.Matchers
import org.mockito.Mockito._
import scala.concurrent.Future
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

class InternalControllerSpec extends ControllerSpec {

  val envelopeID = "00000000-0000-0000-0000-000000000000"
  val oid = "00000001-0000-0000-0000-000000000000"
  val fileName = "test.pdf"
  val testUrl = "http://"
  val cacheMap: CacheMap = CacheMap("", Map("" -> Json.toJson(envelopeID)))

  object TestController extends InternalController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
    override lazy val fileUploadService = mockFileUploadService

  }

  "InternalController" should {
    "use the correct auth connector" in {
      InternalController.authConnector shouldBe FrontendAuthConnector
    }
    "use the correct enrolment connector" in {
      InternalController.enrolmentConnector shouldBe EnrolmentConnector
    }
    "use the correct file upload service" in {
      InternalController.fileUploadService shouldBe FileUploadService
    }
  }

  "A reuests to CloseEnvelope when parameters are passed" should {

    lazy val result = await(TestController.closeEnvelope(tavcReferenceId, envelopeID, oid)(fakeRequest))
    "return a OK" in {
      when(mockFileUploadService.closeEnvelope(Matchers.eq(tavcReferenceId), Matchers.eq(envelopeID), Matchers.eq(oid))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(OK)))

      when(mockFileUploadService.closeEnvelope(Matchers.eq(tavcReferenceId), Matchers.eq(envelopeID), Matchers.eq(oid))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(CREATED)))

      when(mockS4lConnector.saveFormData(Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)

      status(result)  shouldBe OK
    }
  }

}