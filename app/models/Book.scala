package models

import play.api.libs.json.{Json, OFormat}

case class Book(id: Option[Long], title: String, author: String, year: Int)

object Book {
  implicit val bookFormat: OFormat[Book] = Json.format[Book]
}
