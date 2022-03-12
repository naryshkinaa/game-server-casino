package com.example.boot

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.example.rest.{ConnectRoute, HealthRoute, StartGameRoute}
import com.example.service.{GameLobby, SingleCardGameActor, MainLobbyActor}

object Boot extends App {

  // create an actor system for application
  implicit val system: ActorSystem = ActorSystem("simple-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val log = Logging(system, "main")

  val port = 8080

  val singleGameActor = system.actorOf(Props(classOf[GameLobby]), "singleCardGameLobby")
  val doubleGameActor = system.actorOf(Props(classOf[GameLobby]), "doubleCardGameLobby")
  val mainLobby = system.actorOf(Props(classOf[MainLobbyActor], singleGameActor, doubleGameActor), "mainLobby")
  val mainLobby2 = system.actorOf(Props(classOf[SingleCardGameActor], "", doubleGameActor), "mainLobby2")

  val bindingFuture =
    Http()
      .newServerAt("localhost", port)
      .bind(allRoutes)

  log.info(s"Server started at the port $port")

  def allRoutes = {
    concat(
      HealthRoute.healthRoute,
      ConnectRoute.route(mainLobby),
      StartGameRoute.route(mainLobby)
    )
  }
}