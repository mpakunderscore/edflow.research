package controllers

import play.api._
import play.api.libs.json.JsValue
import play.api.mvc._

import play.api.libs.json._


object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }
}