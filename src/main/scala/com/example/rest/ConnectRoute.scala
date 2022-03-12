package com.example.rest

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{as, complete, decodeRequest, entity, path, post}
import akka.http.scaladsl.server.Route
import com.example.domain.ConnectRequest
import com.example.service.MainLobbyActor


object ConnectRoute extends JsonSupport {

  def route(mainLobby: ActorRef): Route =
    path("connect") {
      post {
        decodeRequest {
          entity(as[ConnectRequest]) { request =>
            complete {
              mainLobby ! MainLobbyActor.Connect(request.player)
              "Player connected"
            }
          }
        }
      }
    }
}
