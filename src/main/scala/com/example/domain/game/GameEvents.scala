package com.example.domain.game

import akka.actor.ActorRef

object GameEvents {

  case class PlayerJoin(playerId: String, player: ActorRef)

  case class PlayerExit(playerId: String, player: ActorRef)

}