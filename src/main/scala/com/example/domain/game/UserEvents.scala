package com.example.domain.game

import com.example.domain.PlayerActionType.PlayerActionType

trait UserEvents

case class AutoFoldAction(
                           playerId: String,
                         ) extends UserEvents

case class UserUntypedAction(
                              playerId: String,
                              serializedAction: String
                            ) extends UserEvents