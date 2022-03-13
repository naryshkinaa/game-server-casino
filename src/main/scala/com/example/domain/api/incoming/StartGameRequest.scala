package com.example.domain.api.incoming

import com.example.domain.GameType.GameType
import com.example.domain.GameTypeType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class StartGameRequest(
                             @JsonScalaEnumeration(classOf[GameTypeType]) gameType: GameType
                           ) {
}
