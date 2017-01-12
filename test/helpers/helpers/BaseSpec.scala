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

package controllers.helpers

import common.Constants
import connectors.{EnrolmentConnector, KeystoreConnector, S4LConnector}
import models.fileUpload.{EnvelopeFile, Metadata}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import services.{FileUploadService}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

trait BaseSpec extends UnitSpec with WithFakeApplication with MockitoSugar with FakeRequestHelper with  BeforeAndAfterEach {

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
  val fileOne = EnvelopeFile("1","status","testOne.pdf","pdf","dateCreated",metaData,"href")
  val fileTwo = EnvelopeFile("2","status","testTwo.pdf","pdf","dateCreated",metaData,"href")
  val fileThree = EnvelopeFile("3","status","testThree.pdf","pdf","dateCreated",metaData,"href")
  val fileFour = EnvelopeFile("4","status","testFour.pdf","pdf","dateCreated",metaData,"href")
  val fileFive = EnvelopeFile("5","status","testFive.pdf","pdf","dateCreated",metaData,"href")
  val files = Seq(fileOne,fileTwo)
  val maxFiles = Seq(fileOne,fileTwo,fileThree,fileFour,fileFive)

}
