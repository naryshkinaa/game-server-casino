package com.example.domain.game

import com.example.domain.PlayerActionType.PlayerActionType
import com.example.domain.PlayerActionTypeType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

trait UserEvents

case class AutoFoldAction(
                           playerId: String,
                         ) extends UserEvents

case class UserAction(
                              playerId: String,
                              @JsonScalaEnumeration(classOf[PlayerActionTypeType]) action: PlayerActionType
                            ) extends UserEvents