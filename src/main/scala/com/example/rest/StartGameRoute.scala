package com.example.rest

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{as, complete, decodeRequest, entity, onComplete, path, post}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.example.domain.{ConnectRequest, StartGameRequest}
import com.example.service.MainLobbyActor

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object StartGameRoute extends JsonSupport {
  implicit val timeout: Timeout = 3.seconds

  def route(mainLobby: ActorRef): Route =
    path("start-game") {
      post {
        decodeRequest {
          entity(as[StartGameRequest]) { request =>
              val future = mainLobby
                .ask(MainLobbyActor.NewGame(request.player, request.gameType))
                onComplete(future){
                  case Success(value) => complete(value.toString)
                  case Failure(exception) => sys.error(exception.getMessage)
                }
//                .onComplete{
//                  case
//                }
          }
        }
      }
    }
}
