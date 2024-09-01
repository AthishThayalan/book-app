package controllers

import javax.inject._
import play.api.mvc._
import services.BookService
import models.Book
import play.Logger
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, number, optional, text}
import play.api.i18n.I18nSupport
import play.api.libs.Comet.json
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

case class BookFormData(
    title: String,
    author: String,
    year: Int,
    imageUrl: Option[String]
)

object BookForm {
  val form: Form[BookFormData] = Form(
    mapping(
      "title" -> nonEmptyText,
      "author" -> nonEmptyText,
      "year" -> number,
      "imageUrl" -> optional(text)
    )(BookFormData.apply)(BookFormData.unapply)
  )
}

@Singleton
class BookController @Inject() (
    cc: ControllerComponents,
    bookService: BookService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with I18nSupport {

  def index = Action.async { implicit request: Request[AnyContent] =>
    bookService.listAllBooks().map { books =>
      Ok(views.html.bookList(books))
    }
  }

  def addBook: Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.bookForm(BookForm.form))
  }

  def viewBook(id: Long): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      bookService.getBook(id).map {
        case Some(book) => Ok(views.html.bookView(book))
        case None       => NotFound("Book not found")
      }
  }

  def removeBook(id: Long): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      bookService.deleteBook(id).map { success =>
        if (success) {
          Redirect(routes.BookController.index)
            .flashing("success" -> "Book deleted")
        } else {
          Redirect(routes.BookController.index)
            .flashing("error" -> "Book not found")
        }
      }
  }
  def submitBook: Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      BookForm.form.bindFromRequest.fold(
        formWithErrors =>
          Future.successful(BadRequest(views.html.bookForm(formWithErrors))),
        bookData => {
          val book = Book(
            Some(0),
            bookData.title,
            bookData.author,
            bookData.year,
            bookData.imageUrl
          )
          bookService
            .addBook(book)
            .map { addedBook =>
              Redirect(routes.BookController.index)
                .flashing("success" -> s"Book '${addedBook.title}' added")
            }
            .recover { case ex: Exception =>
              Redirect(routes.BookController.index)
                .flashing("error" -> "Error adding book")
            }
        }
      )
  }

}
