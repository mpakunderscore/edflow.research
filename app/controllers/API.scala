package controllers

import controllers.Application._
import play.api.libs.json.{JsNull, Json, JsValue}
import play.api.mvc.Controller
import play.api.mvc._


/**
 * Created by pavelkuzmin on 16/03/15.
 */
object API extends Controller {

  def getLinks = Action {

    val links: JsValue = Json.obj(
      "name" -> "Watership Down",
      "location" -> Json.obj("lat" -> 51.235685, "long" -> -1.309197),
      "residents" -> Json.arr(
        Json.obj(
          "name" -> "Fiver",
          "age" -> 4,
          "role" -> JsNull
        ),
        Json.obj(
          "name" -> "Bigwig",
          "age" -> 6,
          "role" -> "Owsla"
        )
      )
    )

    Ok(links)
  }
}
