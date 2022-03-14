package com.example.domain.api.incoming

import com.example.domain.game.GameType.GameType
import com.example.domain.game.GameTypeType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class StartGameRequest(
                             @JsonScalaEnumeration(classOf[GameTypeType]) gameType: GameType
                           ) {
}
