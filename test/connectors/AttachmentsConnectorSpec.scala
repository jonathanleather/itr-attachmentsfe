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

/**
  * Copyright 2016 HM Revenue & Customs
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package connectors

import auth.{MockConfig, TAVCUser, ggUser}
import common.{BaseSpec, FakeRequestHelper}
import config.WSHttp
import org.mockito.Matchers
import org.mockito.Mockito._
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.Future
import play.api.test.Helpers._
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }
import uk.gov.hmrc.http.logging.SessionId
import uk.gov.hmrc.http.{ HttpGet, HttpPost, HttpDelete, HttpPut }

class AttachmentsConnectorSpec extends BaseSpec {

  val internalId = "Int-312e5e92-762e-423b-ac3d-8686af27fdb5"

  trait MockHttp extends HttpGet with HttpPost with HttpPut with HttpDelete

  object TestAttachmentsConnector extends AttachmentsConnector with FakeRequestHelper {
    override val serviceUrl = MockConfig.attachmentsUrl
    override val http = mock[MockHttp]
  }

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("1234")))
  implicit val user: TAVCUser = TAVCUser(ggUser.allowedAuthContext, internalId)

  "AttachmentsConnector" should {
    "use the correct http client" in {
      AttachmentsConnector.http shouldBe WSHttp
    }
  }


  "Calling createEnvelope" when {
    "expecting a successful response" should {
      lazy val result = TestAttachmentsConnector.createEnvelope()(hc)
      "return a Status OK (200) response" in {
        when(TestAttachmentsConnector.http.POSTEmpty[HttpResponse](
          Matchers.eq(s"${TestAttachmentsConnector.serviceUrl}/investment-tax-relief-attachments/file-upload/create-envelope"))
          (Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(Future.successful(HttpResponse(OK)))
        await(result) match {
          case response => response.status shouldBe OK
          case _ => fail("No response was received, when one was expected")
        }
      }
    }
  }


  "Calling getEnvelopeStatus" when {
    "expecting a successful response" should {
      lazy val result = TestAttachmentsConnector.getEnvelopeStatus(envelopeId)(hc)
      "return a Status OK (200) response" in {
        when(TestAttachmentsConnector.http.GET[HttpResponse](
          Matchers.eq(s"${TestAttachmentsConnector.serviceUrl}/investment-tax-relief-attachments/file-upload/envelope/$envelopeId/get-envelope-status"))
          (Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(Future.successful(HttpResponse(OK)))
        await(result) match {
          case response => response.status shouldBe OK
          case _ => fail("No response was received, when one was expected")
        }
      }
    }
  }

  "Calling closeEnvelope" when {
    "expecting a successful response" should {
      lazy val result = TestAttachmentsConnector.closeEnvelope(envelopeId)(hc)
      "return a Status OK (200) response" in {
        when(TestAttachmentsConnector.http.POSTEmpty[HttpResponse](
          Matchers.eq(s"${TestAttachmentsConnector.serviceUrl}/investment-tax-relief-attachments/file-upload/envelope/$envelopeId/close-envelope"))
          (Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(Future.successful(HttpResponse(OK)))
        await(result) match {
          case response => response.status shouldBe OK
          case _ => fail("No response was received, when one was expected")
        }
      }
    }
  }


  "Calling deleteFile" when {
    "expecting a successful response" should {
      lazy val result = TestAttachmentsConnector.deleteFile(envelopeId, fileId)(hc)
      "return a Status OK (200) response" in {
        when(TestAttachmentsConnector.http.DELETE[HttpResponse](
          Matchers.eq(s"${TestAttachmentsConnector.serviceUrl}/investment-tax-relief-attachments/file-upload/envelope/$envelopeId/file/$fileId/delete-file"))
          (Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(Future.successful(HttpResponse(OK)))
        await(result) match {
          case response => response.status shouldBe OK
          case _ => fail("No response was received, when one was expected")
        }
      }
    }
  }
}
