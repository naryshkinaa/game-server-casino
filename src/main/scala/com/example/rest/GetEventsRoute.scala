package com.example.rest

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{as, complete, cookie, decodeRequest, entity, get, onComplete, path}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.example.domain.api.incoming.StartGameRequest
import com.example.domain.api.outcoming.{GameConnectedNotification, GetEventsNotification}
import com.example.domain.game.MainLobbyEvents
import com.example.service.PlayerActor.ConnectedToGame
import com.example.util.JsonUtil

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object GetEventsRoute {
  implicit val timeout: Timeout = 3.seconds

  def route(mainLobby: ActorRef): Route =
    cookie("userName") {
      nameCookie =>
        path("get-events") {
          get {
                val future = mainLobby
                  .ask(MainLobbyEvents.GetEvents(nameCookie.value))
                onComplete(future) {
                  case Success(events: GetEventsNotification) =>
                      complete(JsonUtil.toJson(events))
                  case Failure(exception) => sys.error(exception.getMessage)
            }
          }
        }
    }
}
