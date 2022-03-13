package com.example.service

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.example.domain.api.incoming.UserActionRequest
import com.example.domain.api.outcoming.{GameStartedNotification, GetEventsNotification, ResponseType, UserGameResultNotification}
import com.example.domain.game.UserAction
import com.example.domain.{GameResult, Hand, UserPush}

import scala.collection.mutable.ArrayBuffer

class PlayerActor(playerId: String, socketActor: ActorRef) extends Actor with ActorLogging {
  var tokens: Int = 1000
  var games = collection.mutable.Map[String, ActorRef]()

  //see notes implementation, it's temp solution for REST UI
  val gameResults = new ArrayBuffer[UserGameResultNotification]()
  val gameStarted = new ArrayBuffer[GameStartedNotification]()

  import PlayerActor._

  def receive = {
    //FROM server
    case Lose(gameId, amount, message) => {
      tokens -= amount
      games.remove(gameId)
      notifyUser(ResponseType.GameResult, UserGameResultNotification(gameId, GameResult.Lose, message, tokens))
    }
    case Win(gameId, amount, message) => {
      tokens += amount
      games.remove(gameId)
      notifyUser(ResponseType.GameResult, UserGameResultNotification(gameId, GameResult.Win, message, tokens))
    }
    case GameStarted(gameId, hand, isRestarted) => notifyUser(ResponseType.GameStarted, GameStartedNotification(gameId, hand, isRestarted))
    case ConnectedToGame(gameId, game) =>
      //todo need notification
      games.put(gameId, game)

    case GetEvents(callback) => {
      callback ! GetEventsNotification(gameResults.toList, gameStarted.toList)
      gameResults.clear()
      gameStarted.clear()
    }

    //FROM user
    case UserActionRequest(gameId, action) => games.get(gameId).foreach(g => g ! UserAction(playerId, action))
  }

  private def notifyUser(responseType: ResponseType.Value, body: AnyRef): Unit = {
    body match {
      case e: GameStartedNotification => gameStarted += e
      case e: UserGameResultNotification => gameResults += e
    }
    socketActor ! UserPush(responseType, body)
  }
}

object PlayerActor {

  case class Lose(gameId: String, amount: Int, message: String)

  case class Win(gameId: String, amount: Int, message: String)

  case class GameStarted(gameId: String, hand: Hand, isRestarted: Boolean)

  case class ConnectedToGame(gameId: String, game: ActorRef)

  case class GetEvents(callback : ActorRef)

}