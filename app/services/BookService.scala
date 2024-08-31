package services

import models.Book

import javax.inject.Inject
import scala.collection.convert.ImplicitConversions.`collection asJava`
import scala.concurrent.{ExecutionContext, Future}

class BookService @Inject() (implicit ec: ExecutionContext) {
  private var books: Map[Long, Book] = Map()
  private var nextId: Long = 1

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
}
