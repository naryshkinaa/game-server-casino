package com.example.domain.game

import akka.actor.ActorRef
import com.example.domain.GameType.GameType
import com.example.domain.PlayerActionType.PlayerActionType

object MainLobbyEvents {

  case class NewGame(playerId: String, gameType: GameType, callback: Option[ActorRef])

  case class Connect(playerId: String)

  case class Disconnect(playerId: String)

  //tmp solution for rest UI
  case class GetEvents(playerId: String)

  case class UserActionWithPlayer(
                         playerId: String,
                         gameId: String,
                         action: PlayerActionType
                       )

  case class GetGameInfo(playerId: String, gameId: String)

}