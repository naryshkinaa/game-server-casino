package com.example.boot

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.example.service.{GameLobbyActor, MainLobbyActor, SingleCardGameActor}
import com.example.socket.SocketServer

object Boot extends App {

  // create an actor system for application
  implicit val system: ActorSystem = ActorSystem("simple-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val log = Logging(system, "main")



  val singleGameActor = system.actorOf(Props(classOf[GameLobbyActor]), "singleCardGameLobby")
  val doubleGameActor = system.actorOf(Props(classOf[GameLobbyActor]), "doubleCardGameLobby")
  val mainLobby = system.actorOf(Props(classOf[MainLobbyActor], singleGameActor, doubleGameActor), "mainLobby")
  val socketServer = system.actorOf(Props(classOf[SocketServer], mainLobby), "socketServer")

//  val port = 8080
//  def allRoutes = {
//    concat(
//      HealthRoute.healthRoute,
//      ConnectRoute.route(mainLobby),
//      StartGameRoute.route(mainLobby)
//    )
//  }
//  val bindingFuture =
//    Http()
//      .newServerAt("localhost", port)
//      .bind(allRoutes)

  log.info(s"Server started")


}