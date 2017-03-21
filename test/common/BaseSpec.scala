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

import auth.MockAuthConnector
import connectors.{EnrolmentConnector, KeystoreConnector, S4LConnector}
import models.fileUpload.{EnvelopeFile, Metadata}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import services.FileUploadService
import uk.gov.hmrc.play.test.UnitSpec

trait BaseSpec extends UnitSpec with OneAppPerSuite with MockitoSugar with FakeRequestHelper with  BeforeAndAfterEach {

  val mockS4lConnector = mock[S4LConnector]
  val mockEnrolmentConnector = mock[EnrolmentConnector]
  val mockKeyStoreConnector = mock[KeystoreConnector]
  val mockFileUploadService = mock[FileUploadService]


  override def beforeEach() {
    reset(mockS4lConnector)
    reset(mockEnrolmentConnector)
    reset(mockKeyStoreConnector)
  }


  val metaData = Metadata(None)
  val fileOne = EnvelopeFile("1","status","testOne.pdf","pdf", Some(5242880), "dateCreated",metaData,"href")
  val fileTwo = EnvelopeFile("2","status","testTwo.xls","xls",Some(5242880),"dateCreated",metaData,"href")
  val fileThree = EnvelopeFile("3","status","testThree.xlsx","xlsx",Some(5242880),"dateCreated",metaData,"href")
  val fileFour = EnvelopeFile("4","status","testFour.jpg","jpg",Some(5242880),"dateCreated",metaData,"href")
  val fileFive = EnvelopeFile("5","status","testFive.jpeg","jpeg",Some(5242880),"dateCreated",metaData,"href")
  val files = Seq(fileOne,fileTwo)
  val fiveFiles = Seq(fileOne,fileTwo,fileThree,fileFour,fileFive)

  val envelopeId = "1111111111111111111"
  val fileId = "1"


}
