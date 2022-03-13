package com.example

import akka.actor.Props
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.stream.ActorMaterializer
import com.example.bot.AbstractBot
import com.example.bot.strategy.{AgroSingleCardStrategy, BaseSingleCardStrategy}
import com.example.domain.GameType
import com.example.service.lobby.{GameLobbyActor, MainLobbyActor}
import com.example.socket.SocketServer
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.DurationInt

class ConnectAndTestGame extends AnyFunSpec with Matchers with ScalatestRouteTest {

  val mat = ActorMaterializer()
  implicit val keepAlive = 30 seconds

  val singleGameActor = system.actorOf(Props(classOf[GameLobbyActor]), "singleCardGameLobby")
  val doubleGameActor = system.actorOf(Props(classOf[GameLobbyActor]), "doubleCardGameLobby")
  val mainLobby = system.actorOf(Props(classOf[MainLobbyActor], singleGameActor, doubleGameActor), "mainLobby")
  val socketServer = system.actorOf(Props(classOf[SocketServer], mainLobby), "socketServer")

  val wsClient = WSProbe()

  // WS creates a WebSocket request for testing
  val gamesCount = 1000
  val firstClient = system.actorOf(Props(classOf[AbstractBot], "Andrey", 1, gamesCount, true, new BaseSingleCardStrategy(), GameType.SINGLE_CARD_GAME), "Andrey")
  val secondClient = system.actorOf(Props(classOf[AbstractBot], "Tanya", 1, gamesCount, false, new AgroSingleCardStrategy(), GameType.SINGLE_CARD_GAME), "Tanya")

  firstClient ! AbstractBot.Start
  secondClient ! AbstractBot.Start

  Thread.sleep(20000)

}