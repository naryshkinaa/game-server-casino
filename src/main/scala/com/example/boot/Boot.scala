package com.example.boot

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.example.bot.AbstractBot
import com.example.bot.strategy.{AgroSingleCardStrategy, BaseSingleCardStrategy}
import com.example.config.Params
import com.example.domain.GameType
import com.example.rest.{ConnectRoute, GetEventsRoute, GetGameInfoRoute, StartGameRoute, UserActionRoute}
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

  if(Params.startBot){
    val firstClient = system.actorOf(Props(classOf[AbstractBot], "Bot" , Params.botConcurrentGames, 1000000, true, Params.botStrategy, GameType.SINGLE_CARD_GAME), "Bot")
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