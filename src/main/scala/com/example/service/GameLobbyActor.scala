package com.example.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.example.domain.PlayerActionType.PlayerActionType
import com.example.domain.game.GameEvents
import com.example.domain.game.GameLobbyEvents.{GameEnd, GameStart}

class GameLobbyActor(
               ) extends Actor with ActorLogging {
  private val startedGames = collection.mutable.Map[String, ActorRef]()
  private val waitingGames = collection.mutable.Map[String, ActorRef]()
  private val gamesPerPlayer = collection.mutable.Map[String, collection.mutable.Set[String]]()

  import com.example.domain.game.GameLobbyEvents._

  def receive: Receive = {
    case NewGame(playerId, player) =>
      val games = gamesPerPlayer.getOrElseUpdate(playerId, collection.mutable.Set[String]())
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
      gameActor ! GameEvents.PlayerJoin(playerId, player)
    case GameEnd(gameId: String, game: ActorRef) =>
      log.info(s"Game end ${gameId}")
      startedGames.remove(gameId)
    case GameStart(gameId: String, game: ActorRef) =>
      log.info(s"Game start ${gameId}")
      waitingGames.remove(gameId)
      startedGames.put(gameId, game)
  }
}





