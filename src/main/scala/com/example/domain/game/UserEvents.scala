package com.example.domain.game

import PlayerActionType.PlayerActionType

trait UserEvents

case class AutoFoldAction(
                           playerId: String,
                         ) extends UserEvents

case class UserAction(
                       playerId: String,
                       action: PlayerActionType
                     ) extends UserEvents