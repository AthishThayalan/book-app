package services

import models.Book
import play.api.libs.json.Json

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class BookService @Inject() (implicit ec: ExecutionContext) {
  private var books: Map[Long, Book] = loadBooksFromFile()
  private var nextId: Long = (books.keys.maxOption.getOrElse(0L) + 1L)

  def listAllBooks(): Future[Seq[Book]] = Future {
    books.values.toSeq
  }

  def addBook(book: Book): Future[Book] = Future {
    val id = nextId
    nextId += 1
    val newBook = book.copy(id = Some(id))
    books += (id -> newBook)
    newBook
  }

  def getBook(id: Long): Future[Option[Book]] = Future {
    books.get(id)
  }

  def updateBook(id: Long, book: Book): Future[Option[Book]] = Future {
    books.get(id).map { _ =>
      val updatedBook = book.copy(id = Some(id))
      books += (id -> updatedBook)
      updatedBook
    }
  }

  def deleteBook(id: Long): Future[Boolean] = Future {
    books.get(id) match {
      case Some(_) =>
        books -= id
        true
      case None =>
        false
    }
  }

  private def loadBooksFromFile(): Map[Long, Book] = {
    val source = Source.fromFile("conf/books.json")
    val json =
      try source.mkString
      finally source.close()

    Json.parse(json).as[Seq[Book]].map(b => b.id.getOrElse(0L) -> b).toMap

  }
}
