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

package connectors

import common.BaseSpec
import models.fileUpload.EnvelopeFile
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.{CacheMap, SessionCache}
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import play.api.test.Helpers._

import scala.concurrent.Future

class KeystoreConnectorSpec extends BaseSpec {

  object TestKeyStoreConnector extends KeystoreConnector {
    override val sessionCache = mock[SessionCache]
  }

  val testModel = fileOne
  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("1234")))

  "fetchAndGetFormData" should {

    "fetch and get from keystore" in {
      when(TestKeyStoreConnector.sessionCache.fetchAndGetEntry[EnvelopeFile](Matchers.anyString())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(testModel)))
      val result = TestKeyStoreConnector.fetchAndGetFormData[EnvelopeFile]("test")
      await(result) shouldBe Some(testModel)
    }
  }

  "saveFormData" should {
    "save data to keystore" in {
      val returnedCacheMap = CacheMap("test", Map("data" -> Json.toJson(testModel)))
      when(TestKeyStoreConnector.sessionCache.cache[EnvelopeFile](Matchers.anyString(), Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(returnedCacheMap))
      val result = TestKeyStoreConnector.saveFormData("test", testModel)
      await(result) shouldBe returnedCacheMap
    }
  }

  "clearKeystore" should {

    "clear the data from keystore" in {
      when(TestKeyStoreConnector.sessionCache.remove()(Matchers.any[HeaderCarrier]())).thenReturn(Future.successful(HttpResponse(OK)))
      val result = TestKeyStoreConnector.clearKeystore()
      await(result).status shouldBe OK
    }

  }
}