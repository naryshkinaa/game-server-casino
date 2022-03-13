package com.example.domain.game

import akka.actor.ActorRef

object GameLobbyEvents {

  case class NewGame(playerId: String, player: ActorRef)

  case class GameEnd(gameId: String, game: ActorRef)

  case class GameStart(gameId: String, game: ActorRef)

}