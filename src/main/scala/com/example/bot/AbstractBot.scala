package com.example.bot

import akka.actor.{Actor, ActorLogging}
import akka.util.ByteString
import com.example.bot.strategy.CardStrategy
import com.example.config.Params
import com.example.domain.GameType.GameType
import com.example.domain.Hand
import com.example.domain.api.incoming._
import com.example.domain.api.outcoming.{ErrorNotification, GameStartedNotification, UserGameResultNotification, UserInfoNotification}
import com.example.socket.SocketServer
import com.example.util.JsonUtil

import java.net.InetSocketAddress

class AbstractBot(
                   playerId: String,
                   concurrentGames: Int,
                   totalGames: Int,
                   isLogs: Boolean,
                   strategy: CardStrategy,
                   gameType: GameType,
                 ) extends Actor with ActorLogging {

  val client = context.actorOf(Client.props(new InetSocketAddress("localhost", Params.socketPort), null), "BotClient")
  Thread.sleep(1000)

  def receive: Receive = {
    case AbstractBot.Start =>
      client ! prepareRequest(RequestType.Auth, AuthRequest(playerId, "password"))
    case UserInfoNotification(_, _) =>
      context.become(playStage(totalGames, Map()))
      for (_ <- 1 to concurrentGames) {
        self ! AbstractBot.StartGame

      }
    case ErrorNotification(message) =>
      wrapLog(s"Error: $message")

  }

  def playStage(totalGames: Int, games: Map[String, Hand]): Receive = {
    case AbstractBot.StartGame =>
      if (totalGames > 0) {
        context.become(playStage(totalGames - 1, Map()))
        client ! prepareRequest(RequestType.StartGame, StartGameRequest(gameType))
      }

    case GameStartedNotification(gameId, hand: Hand, isRestart) =>
      if (isRestart) wrapLog(s"Equal on showdown. Game restarted $gameId. Rank ${hand.cards.head.rank}")
      else wrapLog(s"Game started $gameId. Rank ${hand.cards.head.rank}")
      val action = strategy.action(hand)
      wrapLog(s"Decide do action: $action")
      client ! prepareRequest(RequestType.GameAction, UserActionRequest(gameId, action))

    case ErrorNotification(message) =>
      wrapLog(s"Error: $message")

    case UserGameResultNotification(gameId,_ , message, balance) =>
      wrapLog(s"Game end: $message. Balance: $balance")
      self ! AbstractBot.StartGame
  }

  private def wrapLog(message: String): Unit = {
    if (isLogs) log.info(message)
  }

  private def prepareRequest(requestType: RequestType.Value, body: AnyRef): ByteString = {
    ByteString.apply(JsonUtil.toJson(WrappedRequest(requestType, JsonUtil.toJson(body))))
  }
}

object AbstractBot {

  case object Start

  private case object StartGame


}