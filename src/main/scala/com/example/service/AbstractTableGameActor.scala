package com.example.service

import akka.actor.{Actor, ActorLogging, ActorRef}

trait AbstractTableGameActor extends Actor with ActorLogging {

  import GameEvents._

  def gameLobby: ActorRef

  def tableSize: Int

  def gameId: String

  def timeLimitSec: Int

  def startGameState(players: Map[String, ActorRef]): Receive

  def waitingState(players: Map[String, ActorRef]): Receive = {
    case Connected(playerId, player) =>
      if (!players.contains(playerId)) {
        if (players.size == tableSize - 1) {
          context.become(startGameState(players + (playerId -> player)))
          gameLobby ! GameLobby.GameStart(gameId, self)

        } else {
          context.become(waitingState(players + (playerId -> player)))
        }
      }
      else {
        gameLobby ! GameLobby.NewGame(playerId, player)
      }
    case Exit(playerId: String, player: ActorRef) =>
      context.become(startGameState(players - playerId))
  }

  def receive = {
    waitingState(Map())
  }
}

