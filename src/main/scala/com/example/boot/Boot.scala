package com.example.boot

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.example.bot.AbstractBot
import com.example.config.Params
import com.example.domain.GameType
import com.example.rest._
import com.example.service.games.{DoubleCardGameActor, SingleCardGameActor}
import com.example.service.lobby.{GameLobbyActor, MainLobbyActor}
import com.example.socket.SocketServer

object Boot extends App {

  // create an actor system for application
  implicit val system: ActorSystem = ActorSystem("simple-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val log = Logging(system, "main")

  val singleGameActor = system.actorOf(Props(classOf[GameLobbyActor], classOf[SingleCardGameActor]), "singleCardGameLobby")
  val doubleGameActor = system.actorOf(Props(classOf[GameLobbyActor], classOf[DoubleCardGameActor]), "doubleCardGameLobby")
  val mainLobby = system.actorOf(Props(classOf[MainLobbyActor], singleGameActor, doubleGameActor), "mainLobby")
  val socketServer = system.actorOf(Props(classOf[SocketServer], mainLobby), "socketServer")

  if (Params.startBot) {
    val firstClient = system.actorOf(Props(classOf[AbstractBot], "Bot", Params.botConcurrentGames, 1000000, true, Params.botStrategy, GameType.SINGLE_CARD_GAME), "Bot")
    firstClient ! AbstractBot.Start
  }

  val bindingFuture =
    Http()
      .newServerAt("localhost", Params.restPort)
      .bind(
        concat(
          ConnectRoute.route(mainLobby),
          StartGameRoute.route(mainLobby),
          GetEventsRoute.route(mainLobby),
          UserActionRoute.route(mainLobby),
          GetGameInfoRoute.route(mainLobby)
        )
      )

  log.info(s"Server started")


}