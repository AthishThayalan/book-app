package controllers

import javax.inject._
import play.api.mvc._
import services.BookService
import models.Book
import org.apache.pekko.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import play.api.libs.json._
import play.mvc.Action

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookController @Inject() (
    cc: ControllerComponents,
    bookService: BookService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def index = Action.async { implicit request: Request[AnyContent] =>
    bookService.listAllBooks().map { books =>
      Ok(views.html.bookList(books))
    }

  }
}
