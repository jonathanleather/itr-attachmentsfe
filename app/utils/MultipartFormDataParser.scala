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

//$COVERAGE-OFF$
package utils

import java.io.ByteArrayOutputStream

import akka.stream.scaladsl.Sink
import akka.util.ByteString
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.FilePart
import play.core.parsers.Multipart.{FileInfo, FilePartHandler}

object MultipartFormDataParser {

  def handleFilePartAsFile: FilePartHandler[ByteString] = {
    case FileInfo(partName, filename, contentType) =>
      val baos = new java.io.ByteArrayOutputStream()
      val sink = Sink.fold[ByteArrayOutputStream, ByteString](baos) {
        (os, data) =>
          os.write(data.toArray)
          os
      }
      val accumulator = Accumulator(sink)
      accumulator.map { case outputStream =>
        FilePart(partName, filename, contentType, ByteString(outputStream.toByteArray))
      }(play.api.libs.concurrent.Execution.defaultContext)
  }

}
//$COVERAGE-ON$
