package com.example.service

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.example.domain.api.incoming.UserActionRequest
import com.example.domain.api.outcoming.{GameStartedNotification, ResponseType, UserGameResultNotification}
import com.example.domain.game.UserUntypedAction
import com.example.domain.{GameResult, Hand, UserPush}

class PlayerActor(playerId: String, socketActor: ActorRef) extends Actor with ActorLogging {
  var tokens: Int = 1000
  var games = collection.mutable.Map[String, ActorRef]()

  import PlayerActor._

  def receive = {
    //FROM server
    case Lose(gameId, amount, message) => {
      tokens -= amount
      games.remove(gameId)
      notifyUser(ResponseType.GameResult, UserGameResultNotification(GameResult.Lose, message, tokens))
    }
    case Win(gameId, amount, message) => {
      tokens += amount
      games.remove(gameId)
      notifyUser(ResponseType.GameResult, UserGameResultNotification(GameResult.Win, message, tokens))
    }
    case GameStarted(gameId, hand, isRestarted) => notifyUser(ResponseType.GameStarted, GameStartedNotification(gameId, hand, isRestarted))
    case ConnectedToGame(gameId, game) =>
      //todo need notification
      games.put(gameId, game)

    //FROM user
    case UserActionRequest(gameId, data) => games.get(gameId).foreach(g => g ! UserUntypedAction(playerId, data))
  }

  private def notifyUser(responseType: ResponseType.Value, body: AnyRef): Unit = {
    socketActor ! UserPush(responseType, body)
  }
}

object PlayerActor {

  case class Lose(gameId: String, amount: Int, message: String)

  case class Win(gameId: String, amount: Int, message: String)

  case class GameStarted(gameId: String, hand: Hand, isRestarted: Boolean)

  case class ConnectedToGame(gameId: String, game: ActorRef)

}