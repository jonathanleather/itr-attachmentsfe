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

object FileHelper extends FileHelper {

}

trait FileHelper {

  final val PDF = "application/pdf"
  final val XLS = "application/vnd.ms-excel"
  final val XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
  final val JPG = "image/jpeg"
  final val DEFAULT = "application/octet-stream"

  def getMimeType(filename: String) : String = {

    filename.split('.').drop(1).lastOption match {
      case Some(ext) => ext.toLowerCase() match {
        case "pdf" => PDF
        case "jpg" => JPG
        case "jpeg" => JPG
        case "xls" => XLS
        case "xlsx" => XLSX
        case _ => DEFAULT
      }
      case None => DEFAULT
    }
  }

  def isAllowableFileType(fileName:String): Boolean = {
     fileName.matches("""^.*\.(jpg|JPG|jpeg|JPEG|xls|XLS|pdf|PDF|xlsx|XLSX)$""")
  }

}
