package com.example.domain.api.incoming

import com.example.domain.PlayerActionType.PlayerActionType
import com.example.domain.PlayerActionTypeType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class UserActionRequest(
                              gameId: String,
                              @JsonScalaEnumeration(classOf[PlayerActionTypeType]) action: PlayerActionType
                            )
