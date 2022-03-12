package com.example.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.example.domain.PlayerActionType.PlayerActionType

class GameLobby(
               ) extends Actor with ActorLogging {
  private val startedGames = collection.mutable.Map[String, ActorRef]()
  private val waitingGames = collection.mutable.Map[String, ActorRef]()
  private val playerGames = collection.mutable.Map[String, collection.mutable.Set[String]]()

  import GameLobby._

  def receive: Receive = {
    case NewGame(playerId, player) =>
      val games = playerGames.getOrElseUpdate(playerId, collection.mutable.Set[String]())
      val gameActor: ActorRef = waitingGames
        .find(g => !games.contains(g._1))
        .map(_._2)
        .getOrElse {
          val gameId: String = java.util.UUID.randomUUID.toString
          val newGame = context.actorOf(Props(classOf[SingleCardGameActor], gameId, self))
          waitingGames.put(gameId, newGame)
          games.add(gameId)
          newGame
        }
      gameActor ! GameEvents.Connected(playerId, player)
    case GameEnd(gameId: String, game: ActorRef) =>
      log.info(s"Game end ${gameId}")
      startedGames.remove(gameId)
    case GameStart(gameId: String, game: ActorRef) =>
      log.info(s"Game start ${gameId}")
      waitingGames.remove(gameId)
      startedGames.put(gameId, game)
  }
}

object GameLobby {

  case class NewGame(playerId: String, player: ActorRef)

  case class GameEnd(gameId: String, game: ActorRef)

  case class GameStart(gameId: String, game: ActorRef)

}

object GameEvents {

  case class Connected(playerId: String, player: ActorRef)

  case class Exit(playerId: String, player: ActorRef)

  trait UserAction
  case class FoldPlayAction(action: PlayerActionType, playerId: String) extends UserAction

}
