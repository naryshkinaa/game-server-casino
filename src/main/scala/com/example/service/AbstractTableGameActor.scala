package com.example.service

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.example.domain.game.GameLobbyEvents

trait AbstractTableGameActor extends Actor with ActorLogging {

  import com.example.domain.game.GameEvents._

  def gameLobby: ActorRef

  def tableSize: Int

  def gameId: String

  def timeLimitSec: Int

  def startGameState(players: Map[String, ActorRef], isRestarted: Boolean): Receive

  def waitingState(players: Map[String, ActorRef]): Receive = {
    case PlayerJoin(playerId, player, callback) =>
      if (!players.contains(playerId)) {
        player ! PlayerActor.ConnectedToGame(gameId, self)
        callback ! PlayerActor.ConnectedToGame(gameId, self)
        if (players.size == tableSize - 1) {
          context.become(startGameState(players + (playerId -> player), false))
          //note possible chance that new Player (at client) does not complete process Connected to Game message
          gameLobby ! GameLobbyEvents.GameStart(gameId, self)
        } else {
          context.become(waitingState(players + (playerId -> player)))
        }
      }
      else {
        gameLobby ! GameLobbyEvents.NewGame(playerId, player, callback)
      }
    case PlayerExit(playerId: String, player: ActorRef) =>
      context.become(waitingState(players - playerId))
  }

  def receive = {
    waitingState(Map())
  }
}

