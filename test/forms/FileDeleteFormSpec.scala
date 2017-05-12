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

package forms

import forms.FileDeleteForm._
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import models.fileUpload.FileDeleteModel
import play.api.libs.json.Json

class FileDeleteFormSpec extends UnitSpec with OneAppPerSuite {

  val testId = "00000-ffff-99999"
  val model = new FileDeleteModel(testId)
  val modelJson = """{"fileID":"00000-ffff-99999"}"""

  // model to json
  "The Had Previous RFI Form model" should {
    "load convert to JSON successfully" in {

      implicit val formats = Json.format[FileDeleteModel]
      val deleteModelJson= Json.toJson(model).toString()

      println(deleteModelJson)
      deleteModelJson shouldBe modelJson
    }
  }

  // form model to json - apply
  "The Had Previous RFI Form model" should {
    "call apply correctly on the model" in {
      implicit val formats = Json.format[FileDeleteModel]
      val fileDelteForm = fileDeleteForm.fill(model)
      fileDelteForm.get.fileID shouldBe testId
    }

    // form json to model - unapply
    "call unapply successfully to create expected Json" in {
      implicit val formats = Json.format[FileDeleteModel]
      val fileDelteForm = FileDeleteForm.fileDeleteForm.fill(model)
      val formJson = Json.toJson(fileDelteForm.get).toString()
      println()
      formJson shouldBe modelJson
    }
  }

}

