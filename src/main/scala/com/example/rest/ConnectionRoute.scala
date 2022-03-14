package com.example.rest

import akka.actor.ActorRef
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.example.domain.api.incoming.AuthRequest
import com.example.domain.api.outcoming.response.UserInfoResponse
import com.example.service.lobby.MainLobbyActor
import com.example.util.JsonUtil

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object ConnectRoute {
  implicit val timeout: Timeout = 3.seconds

  def route(mainLobby: ActorRef): Route =
    path("connect") {
      post {
        decodeRequest {
          entity(as[String]) { request =>
            val parsed = JsonUtil.fromJson[AuthRequest](request)
            val future = mainLobby
              .ask(MainLobbyActor.Connect(parsed.player))
            onComplete(future) {
              case Success(info: UserInfoResponse) =>
                setCookie(HttpCookie("userName", value = parsed.player)) {
                  complete(JsonUtil.toJson(info))
                }
              case Failure(exception) => sys.error(exception.getMessage)
            }
          }
        }
      }
    }
}
