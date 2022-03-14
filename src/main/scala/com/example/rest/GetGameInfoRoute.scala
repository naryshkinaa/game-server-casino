package com.example.rest

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{as, complete, cookie, decodeRequest, entity, onComplete, path, post}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.example.domain.api.incoming.GetGameInfoRequest
import com.example.domain.api.outcoming.response.GameInfoResponse
import com.example.service.lobby.MainLobbyActor
import com.example.util.JsonUtil

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object GetGameInfoRoute {
  implicit val timeout: Timeout = 3.seconds

  def route(mainLobby: ActorRef): Route =
    cookie("userName") {
      nameCookie =>
        path("get-game-info") {
          post {
            decodeRequest {
              entity(as[String]) { request =>
                val parsed = JsonUtil.fromJson[GetGameInfoRequest](request)
                val future = mainLobby.ask(MainLobbyActor.GetGameInfo(nameCookie.value, parsed.gameId))
                onComplete(future) {
                  case Success(info: GameInfoResponse) =>
                    complete(JsonUtil.toJson(info))
                  case Failure(exception) => sys.error(exception.getMessage)
                }
              }
            }
          }
        }
    }
}
