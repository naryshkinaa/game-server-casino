package com.example.service

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.example.domain.notifications.UserGameResult
import com.example.domain.{GameResult, Hand}

class PlayerActor() extends Actor with ActorLogging {
  var tokens: Int = 1000
  var games = collection.mutable.Map[String, ActorRef]()

  import PlayerActor._

  def receive = {
    case Lose(gameId, amount, message) => {
      tokens -= amount
      games.remove(gameId)
      notifyUser(UserGameResult(GameResult.Lose, message, tokens))
    }
    case Win(gameId, amount, message) => {
      tokens += amount
      games.remove(gameId)
      notifyUser(UserGameResult(GameResult.Win, message, tokens))
    }
    case g: GameStarted => notifyUser(g)
    case Connect(gameId, game) => games.put(gameId, game)
  }

  private def notifyUser(message: AnyRef): Unit = {
    log.info(message.toString)
  }
}

object PlayerActor {

  case class Lose(gameId: String, amount: Int, message: String)

  case class Win(gameId: String, amount: Int, message: String)

  case class GameStarted(gameId: String, hand: Hand)

  case class Connect(gameId: String, game: ActorRef)

}