package com.example.domain

import com.fasterxml.jackson.core.`type`.TypeReference

object GameType extends Enumeration {
  type GameType = Value
  val SINGLE_CARD_GAME, DOUBLE_CARD_GAME = Value
}

class GameTypeType extends TypeReference[GameType.type]