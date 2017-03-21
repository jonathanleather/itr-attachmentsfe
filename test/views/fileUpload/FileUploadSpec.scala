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

package views.fileUpload

import common.BaseSpec
import org.jsoup.Jsoup
import play.api.i18n.Messages
import views.html.fileUpload.FileUpload
import play.api.i18n.Messages.Implicits._

class FileUploadSpec extends BaseSpec {

  val envelopeID = "00000000-0000-0000-0000-000000000000"
  val backUrl = "http://test/back"

  "The FileUpload page" should {

    "contain the correct elements when loaded with no files" in {
      lazy val page = FileUpload(Seq(), envelopeID,backUrl)(fakeRequest, applicationMessages)
      lazy val document = Jsoup.parse(page.body)

      //title and heading
      document.title() shouldBe Messages("page.fileUpload.title")

      //sidebar
      document.body.getElementById("supporting-docs-heading").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.heading")
      document.body.getElementById("supporting-docs-one").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.one")
      document.body.getElementById("supporting-docs-two").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.two")
      document.body.getElementById("supporting-docs-three").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.three")
      document.body.getElementById("supporting-docs-four").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.four")
      document.body.getElementById("supporting-docs-five").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.five")

      document.body.getElementById("back-link").attr("href") shouldEqual backUrl
      document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.company.details.five")


      document.body.getElementById("main-heading").text() shouldBe Messages("page.fileUpload.heading")
      document.body.getElementById("file-limit-restriction").text() shouldBe Messages("page.fileUpload.restriction")
      document.body.getElementById("file-condition-size").text() shouldBe Messages("page.fileUpload.condition.size")
      document.body.getElementById("file-condition-types").text() shouldBe Messages("page.fileUpload.condition.types")
      document.body.getElementById("file-condition-macros").text() shouldBe Messages("page.fileUpload.condition.macros")
      document.body.getElementById("file-limit-hint").text() shouldBe Messages("page.fileUpload.hint")


      //file table should not exist
      intercept[NullPointerException] {
        val filesTable = document.getElementById("files-table").select("tbody")
      }

       //Dynamic button
      document.body.getElementById("upload-button").text() shouldBe Messages("page.fileUpload.upload")


    }

    "contain the correct elements when loaded with one or more files" in {

      lazy val page = FileUpload(files, envelopeID,"http://test/back")(fakeRequest, applicationMessages)
      lazy val document = Jsoup.parse(page.body)

      //title and heading
      document.title() shouldBe Messages("page.fileUpload.title")

      //sidebar
      document.body.getElementById("supporting-docs-heading").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.heading")
      document.body.getElementById("supporting-docs-one").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.one")
      document.body.getElementById("supporting-docs-two").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.two")
      document.body.getElementById("supporting-docs-three").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.three")
      document.body.getElementById("supporting-docs-four").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.four")
      document.body.getElementById("supporting-docs-five").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.five")

      document.body.getElementById("back-link").attr("href") shouldEqual backUrl
      document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.company.details.five")

      document.body.getElementById("main-heading").text() shouldBe Messages("page.fileUpload.heading")
      document.body.getElementById("file-limit-restriction").text() shouldBe Messages("page.fileUpload.restriction")
      document.body.getElementById("file-condition-size").text() shouldBe Messages("page.fileUpload.condition.size")
      document.body.getElementById("file-condition-types").text() shouldBe Messages("page.fileUpload.condition.types")
      document.body.getElementById("file-condition-macros").text() shouldBe Messages("page.fileUpload.condition.macros")
      document.body.getElementById("file-limit-hint").text() shouldBe Messages("page.fileUpload.hint")


      //file table
      lazy val filesTable = document.getElementById("files-table").select("tbody")
      filesTable.select("tr").get(0).getElementById("file-0").text() shouldBe "testOne.pdf"
      filesTable.select("tr").get(0).getElementById("remove-0").text() shouldBe Messages("page.fileUpload.remove")
      filesTable.select("tr").get(1).getElementById("file-1").text() shouldBe "testTwo.xls"
      filesTable.select("tr").get(1).getElementById("remove-1").text() shouldBe Messages("page.fileUpload.remove")

      document.body.getElementById("continue-link").text() shouldBe Messages("page.fileUpload.snc")

    }

    "contain the correct elements when loaded with 5 files" in {

      lazy val page = FileUpload(fiveFiles, envelopeID, "http://test/back")(fakeRequest, applicationMessages)
      lazy val document = Jsoup.parse(page.body)

      //title and heading
      document.title() shouldBe Messages("page.fileUpload.title")

      //sidebar
      document.body.getElementById("supporting-docs-heading").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.heading")
      document.body.getElementById("supporting-docs-one").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.one")
      document.body.getElementById("supporting-docs-two").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.two")
      document.body.getElementById("supporting-docs-three").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.three")
      document.body.getElementById("supporting-docs-four").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.four")
      document.body.getElementById("supporting-docs-five").text() shouldBe Messages("page.supportingDocuments.SupportingDocuments.bullet.five")


      document.body.getElementById("main-heading").text() shouldBe Messages("page.fileUpload.heading")
      document.body.getElementById("file-limit-restriction").text() shouldBe Messages("page.fileUpload.restriction")
      document.body.getElementById("file-condition-size").text() shouldBe Messages("page.fileUpload.condition.size")
      document.body.getElementById("file-condition-types").text() shouldBe Messages("page.fileUpload.condition.types")
      document.body.getElementById("file-condition-macros").text() shouldBe Messages("page.fileUpload.condition.macros")
      document.body.getElementById("file-limit-hint").text() shouldBe Messages("page.fileUpload.hint")

      //file table
      lazy val filesTable = document.getElementById("files-table").select("tbody")
      filesTable.select("tr").get(0).getElementById("file-0").text() shouldBe "testOne.pdf"
      filesTable.select("tr").get(0).getElementById("remove-0").text() shouldBe Messages("page.fileUpload.remove")
      filesTable.select("tr").get(1).getElementById("file-1").text() shouldBe "testTwo.xls"
      filesTable.select("tr").get(1).getElementById("remove-1").text() shouldBe Messages("page.fileUpload.remove")
      filesTable.select("tr").get(2).getElementById("file-2").text() shouldBe "testThree.xlsx"
      filesTable.select("tr").get(2).getElementById("remove-2").text() shouldBe Messages("page.fileUpload.remove")
      filesTable.select("tr").get(3).getElementById("file-3").text() shouldBe "testFour.jpg"
      filesTable.select("tr").get(3).getElementById("remove-3").text() shouldBe Messages("page.fileUpload.remove")
      filesTable.select("tr").get(4).getElementById("file-4").text() shouldBe "testFive.jpeg"
      filesTable.select("tr").get(4).getElementById("remove-4").text() shouldBe Messages("page.fileUpload.remove")

      document.body.getElementById("continue-link").text() shouldBe Messages("page.fileUpload.snc")

    }
  }

}
