package controllers

import javax.inject._
import play.api.mvc._
import services.BookService
import models.Book
import play.api.i18n.I18nSupport
import play.api.libs.Comet.json
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

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

  def viewBook(id: Long) = Action.async {
    implicit request: Request[AnyContent] =>
      bookService.getBook(id).map {
        case Some(book) => Ok(views.html.bookView(book))
        case None       => NotFound("Book not found")
      }
  }

}
