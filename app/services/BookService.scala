package services

import models.Book
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class BookService @Inject() (implicit ec: ExecutionContext) {

  private def readBooksFromFile(): Seq[Book] = {
    val source = scala.io.Source.fromFile(
      "conf/books.json"
    ) // Returns a source object of the json
    val json =
      try source.mkString // concatenates all char's from the json into one string
      finally source.close()
    Json
      .parse(json)
      .as[Seq[
        Book
      ]] // That Json string is converted to a sequence of "Book" Objects
  }

  private def writeBooksToFile(books: Seq[Book]): Unit = {
    val json =
      Json
        .toJson(books)
        .toString() // converts the sequence of book objects into a JSON string.
    Files.write(
      Paths.get("conf/books.json"), // Path object representing the file object
      json.getBytes(StandardCharsets.UTF_8), // converts JSON string into bytes
      StandardOpenOption.TRUNCATE_EXISTING, // If file exists it is overwritten
      StandardOpenOption.CREATE // else create it
    )
  }

  def listAllBooks(): Future[Seq[Book]] = Future {
    readBooksFromFile()
  }

  def addBook(book: Book): Future[Book] = Future {
    val books = readBooksFromFile()
    val id = books.map(_.id.getOrElse(0L)).maxOption.getOrElse(0L) + 1L
    val newBook = book.copy(id = Some(id))
    writeBooksToFile(books :+ newBook) // call the method to write to books.json
    newBook
  }

  def getBook(id: Long): Future[Option[Book]] = Future {
    readBooksFromFile().find(
      _.id.contains(id)
    ) // returns the book if it exists in the json file
  }

  def updateBook(id: Long, book: Book): Future[Option[Book]] = Future {
    val books = readBooksFromFile() // seq of book objects
    val updatedBooks = books.map {
      case b if b.id.contains(id) => book.copy(id = Some(id))
      case b                      => b
    }
    writeBooksToFile(updatedBooks)
    updatedBooks.find(_.id.contains(id))
  }

  def deleteBook(id: Long): Future[Boolean] = Future {
    val books = readBooksFromFile()
    val updatedBooks = books.filterNot(_.id.contains(id))
    if (books.size != updatedBooks.size) { // if book was removed
      writeBooksToFile(updatedBooks)
      true
    } else {
      false
    }
  }
}
