package com.example.rest

import akka.actor.ActorRef
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives.{as, complete, cookie, decodeRequest, entity, onComplete, path, post, setCookie}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.example.domain.api.incoming.StartGameRequest
import com.example.domain.api.outcoming.{GameConnectedNotification, UserInfoNotification}
import com.example.domain.game.MainLobbyEvents
import com.example.service.player.PlayerActor.ConnectedToGame
import com.example.socket.SocketHandler.PlayerInfo
import com.example.util.JsonUtil

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object StartGameRoute {
  implicit val timeout: Timeout = 3.seconds

  def route(mainLobby: ActorRef): Route =
    cookie("userName") {
      nameCookie =>
        path("start-game") {
          post {
            decodeRequest {
              entity(as[String]) { request =>
                val parsed = JsonUtil.fromJson[StartGameRequest](request)
                val future = mainLobby
                  .ask(MainLobbyEvents.NewGame(nameCookie.value, parsed.gameType, None))
                onComplete(future) {
                  case Success(connected: ConnectedToGame) =>
                      complete(JsonUtil.toJson(GameConnectedNotification(connected.gameId)))
                  case Failure(exception) => sys.error(exception.getMessage)
                }
              }
            }
          }
        }
    }
}
