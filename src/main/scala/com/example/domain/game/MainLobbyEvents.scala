package com.example.domain.game

import com.example.domain.GameType.GameType

object MainLobbyEvents {

  case class NewGame(playerId: String, gameType: GameType)

  case class Connect(playerId: String)

  case class Disconnect(playerId: String)

}