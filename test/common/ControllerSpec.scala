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

import auth.authModels.UserIDs
import auth.{Enrolment, Identifier, TAVCUser, ggUser}
import org.mockito.Matchers
import org.mockito.Mockito._

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

trait ControllerSpec extends BaseSpec {

  val tavcReferenceId = "XATAVC000123456"
  val internalId = "Int-312e5e92-762e-423b-ac3d-8686af27fdb5"

  def mockEnrolledRequest(): Unit = {
    when(mockEnrolmentConnector.getTAVCEnrolment(Matchers.any())(Matchers.any()))
      .thenReturn(Future.successful(Option(Enrolment("HMRC-TAVC-ORG", Seq(Identifier("TavcReference", "1234")), "Activated"))))
    when(mockEnrolmentConnector.getTavcReferenceNumber(Matchers.any())(Matchers.any()))
      .thenReturn(Future.successful(tavcReferenceId))
    //val userIds: UserIDs = UserIDs("Int-312e5e92-762e-423b-ac3d-8686af27fdb5", "Ext-312e5e92-762e-423b-ac3d-8686af27fdb5")
//    when(mockAuthConnector.getIds[UserIDs](Matchers.any())).thenReturn(userIds)
    //when(auth.MockAuthConnector.getIds[UserIDs](Matchers.any())).thenReturn(userIds)
  }

  def mockNotEnrolledRequest(): Unit = when(mockEnrolmentConnector.getTAVCEnrolment(Matchers.any())(Matchers.any()))
    .thenReturn(Future.successful(None))

  implicit val hc = HeaderCarrier()
  implicit val user = TAVCUser(ggUser.allowedAuthContext, internalId)
}
