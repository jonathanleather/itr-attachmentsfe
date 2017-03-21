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

object Constants extends Constants

trait Constants {
  val StandardRadioButtonYesValue = "Yes"
  val StandardRadioButtonNoValue = "No"
  val enrolmentOrgKey = "HMRC-TAVC-ORG"
  val enrolmentTavcRefKey = "TAVCRef"
  val fileSizeLimit =10481664
  val numberOfFilesLimit = 57
  val envelopeLimit = 26214400 //25MB
}
