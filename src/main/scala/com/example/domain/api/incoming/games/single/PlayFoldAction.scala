package com.example.domain.api.incoming.games.single

import com.example.domain.PlayerActionType.PlayerActionType
import com.example.domain.PlayerActionTypeType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class PlayFoldAction(
                           @JsonScalaEnumeration(classOf[PlayerActionTypeType]) action: PlayerActionType
                         )