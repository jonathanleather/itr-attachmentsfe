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

import uk.gov.hmrc.play.test.UnitSpec


class FileHelperSpec extends UnitSpec {


  "Calling  DateFormatter toDateString" should {
    "return the pdf application mime correctly if lowercase" in {
      FileHelper.getMimeType("test.pdf") shouldBe FileHelper.PDF
    }
    "return the pdf application mime correctly if uppercase" in {
      FileHelper.getMimeType("test.PDF") shouldBe FileHelper.PDF
    }
    "return the pdf application mime correctly if mixed case" in {
      FileHelper.getMimeType("test.PdF") shouldBe FileHelper.PDF
    }
    "return the xls application mime correctly if lowercase" in {
      FileHelper.getMimeType("test.xls") shouldBe FileHelper.XLS
    }
    "return the xls application mime correctly if uppercase" in {
      FileHelper.getMimeType("test.XLS") shouldBe FileHelper.XLS
    }
    "return the xls application mime correctly if mixed case" in {
      FileHelper.getMimeType("test.xLs") shouldBe FileHelper.XLS
    }
    "return the xlsx application mime correctly if lowercase" in {
      FileHelper.getMimeType("test.xlsx") shouldBe FileHelper.XLSX
    }
    "return the xlsx application mime correctly if uppercase" in {
      FileHelper.getMimeType("test.XLSX") shouldBe FileHelper.XLSX
    }
    "return the xlsx application mime correctly if mixed case" in {
      FileHelper.getMimeType("test.XlsX") shouldBe FileHelper.XLSX
    }
    "return the jpg application mime correctly if lowercase" in {
      FileHelper.getMimeType("test.jpg") shouldBe FileHelper.JPG
    }
    "return the jpg application mime correctly if uppercase" in {
      FileHelper.getMimeType("test.JPG") shouldBe FileHelper.JPG
    }
    "return the jpg application mime correctly if mixed case" in {
      FileHelper.getMimeType("test.JpG") shouldBe FileHelper.JPG
    }
    "return the jpeg application mime correctly if lowercase" in {
      FileHelper.getMimeType("test.jpeg") shouldBe FileHelper.JPG
    }
    "return the jpeg application mime correctly if uppercase" in {
      FileHelper.getMimeType("test.JPEG") shouldBe FileHelper.JPG
    }
    "return the jpeg application mime correctly if mixed case" in {
      FileHelper.getMimeType("test.JpeG") shouldBe FileHelper.JPG
    }

  }

  "Calling DateFormatter toDateString" should {
    "return the correct pdf application mime correctly if filename has multiple . delimiters" in {
      FileHelper.getMimeType("test.file.extensions.pdf") shouldBe FileHelper.PDF
    }
    "return the correct xls application mime correctly if filename has multiple . delimiters" in {
      FileHelper.getMimeType("test.file.extensions.xls") shouldBe FileHelper.XLS
    }
    "return the correct xlsx application mime correctly if filename has multiple . delimiters" in {
      FileHelper.getMimeType("test.file.extensions.xlsx") shouldBe FileHelper.XLSX
    }
    "return the correct jpg application mime correctly if filename has multiple . delimiters" in {
      FileHelper.getMimeType("test.file.extensions.jpg") shouldBe FileHelper.JPG
    }
    "return the correct jpeg application mime correctly if filename has multiple . delimiters" in {
      FileHelper.getMimeType("test.file.extensions.jpeg") shouldBe FileHelper.JPG
    }

  }

  "Calling DateFormatter toDateString" should {
    "return the correct application mime correctly if there is no file extension" in {
      FileHelper.getMimeType("myfile") shouldBe FileHelper.DEFAULT
    }
    "return the correct application mime correctly if an onknown file extension" in {
      FileHelper.getMimeType("test.file.extensions") shouldBe FileHelper.DEFAULT
    }
    "return the correct application mime correctly if an empty file extension" in {
      FileHelper.getMimeType("") shouldBe FileHelper.DEFAULT
    }
  }


  "Calling DateFormatter toDateString" should {
    "return the correct value for a file with a pdf extension" in {
      FileHelper.isAllowableFileType("test.pdf") shouldBe true
    }
    "return the correct value for a file with a pdf uppercase extension" in {
      FileHelper.isAllowableFileType("test.PDF") shouldBe true
    }
    "return the correct value for a file with a xls extension" in {
      FileHelper.isAllowableFileType("test.xls") shouldBe true
    }
    "return the correct value for a file with a xls uppercase extension" in {
      FileHelper.isAllowableFileType("test.XLS") shouldBe true
    }


    "return the correct value for a file with pdf in the filename but not as a valid extension" in {
      FileHelper.isAllowableFileType("test.pdf.doc") shouldBe false
    }
    "return the correct value for a file with xls in the filename but not as a valid extension" in {
      FileHelper.isAllowableFileType("test.xls.doc") shouldBe false
    }
    "return the correct value for a file with xlsx in the filename but not as a valid extension" in {
      FileHelper.isAllowableFileType("test.xlsx.doc") shouldBe false
    }
    "return the correct value for a file with jpg in the filename but not as a valid extension" in {
      FileHelper.isAllowableFileType("test.jpg.doc") shouldBe false
    }
    "return the correct value for a file with jpeg in the filename but not as a valid extension" in {
      FileHelper.isAllowableFileType("test.jpeg.doc") shouldBe false
    }

  }

}
