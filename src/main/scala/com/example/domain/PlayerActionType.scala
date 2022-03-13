package com.example.domain

import com.fasterxml.jackson.core.`type`.TypeReference

object PlayerActionType extends Enumeration {
  type PlayerActionType = Value
  val FOLD, PLAY = Value

}

class PlayerActionTypeType extends TypeReference[PlayerActionType.type]