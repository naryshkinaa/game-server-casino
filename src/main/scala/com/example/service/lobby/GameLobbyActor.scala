package com.example.service.lobby

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.example.service.games.AbstractTableGameActor

class GameLobbyActor(
                      gameClass: Class[_]
                    ) extends Actor with ActorLogging {
  private val startedGames = collection.mutable.Map[String, ActorRef]()
  private val waitingGames = collection.mutable.Map[String, ActorRef]()
  private val gamesPerPlayer = collection.mutable.Map[String, collection.mutable.Set[String]]()

  def receive: Receive = {
    case GameLobbyActor.NewGame(playerId, player, callback) =>
      val games = gamesPerPlayer.getOrElseUpdate(playerId, collection.mutable.Set[String]())
      val (gameId, gameActor) = waitingGames
        .find(g => !games.contains(g._1))
        .getOrElse {
          val gameId: String = java.util.UUID.randomUUID.toString
          val newGame = context.actorOf(Props(gameClass, gameId, self))
          waitingGames.put(gameId, newGame)
          games.add(gameId)
          (gameId, newGame)
        }
      gameActor ! AbstractTableGameActor.PlayerJoin(playerId, player, callback)
    case GameLobbyActor.GameEnd(gameId: String, game: ActorRef) =>
      log.info(s"Game end $gameId")
      startedGames.remove(gameId)
    case GameLobbyActor.GameStart(gameId: String, game: ActorRef) =>
      log.info(s"Game start $gameId")
      waitingGames.remove(gameId)
      startedGames.put(gameId, game)
  }
}

object GameLobbyActor {

  case class NewGame(playerId: String, player: ActorRef, callback: ActorRef)

  case class GameEnd(gameId: String, game: ActorRef)

  case class GameStart(gameId: String, game: ActorRef)

}



