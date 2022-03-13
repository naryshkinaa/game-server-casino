package com.example.domain

import com.fasterxml.jackson.core.`type`.TypeReference

object GameResult extends Enumeration{
  type GameResult = Value
  val Win, Lose, Equal = Value
}


class GameResultType extends TypeReference[GameResult.type]