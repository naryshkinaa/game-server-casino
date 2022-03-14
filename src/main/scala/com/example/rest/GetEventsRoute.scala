package com.example.rest

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{complete, cookie, get, onComplete, path}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.example.domain.api.outcoming.response.GetEventsResponse
import com.example.service.lobby.MainLobbyActor
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
              .ask(MainLobbyActor.GetEvents(nameCookie.value))
            onComplete(future) {
              case Success(events: GetEventsResponse) =>
                complete(JsonUtil.toJson(events))
              case Failure(exception) => sys.error(exception.getMessage)
            }
          }
        }
    }
}
