package models

import play.api.libs.json.{Json, OFormat}

case class Book(
    id: Option[Long],
    title: String,
    author: String,
    year: Int,
    imageUrl: Option[String]
)

object Book {
  implicit val bookFormat: OFormat[Book] = Json.format[Book]
}
