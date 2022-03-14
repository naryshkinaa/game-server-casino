package com.example.rest

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{as, complete, cookie, decodeRequest, entity, path, post}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.example.domain.api.incoming.UserActionRequest
import com.example.service.lobby.MainLobbyActor
import com.example.util.JsonUtil

import scala.concurrent.duration.DurationInt


object UserActionRoute {
  implicit val timeout: Timeout = 3.seconds

  def route(mainLobby: ActorRef): Route =
    cookie("userName") {
      nameCookie =>
        path("user-action") {
          post {
            decodeRequest {
              entity(as[String]) { request =>
                val parsed = JsonUtil.fromJson[UserActionRequest](request)
                mainLobby ! MainLobbyActor.UserAction(nameCookie.value, parsed.gameId, parsed.action)
                complete("{}")
              }
            }
          }
        }
    }
}
