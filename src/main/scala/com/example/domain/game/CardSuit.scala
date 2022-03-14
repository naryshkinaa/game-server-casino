package com.example.domain.game

import com.fasterxml.jackson.core.`type`.TypeReference

object CardSuit extends Enumeration {
  type CardSuit = Value
  val Diamond, Clubs, Hearts, Spades = Value
}

class CardSuitType extends TypeReference[CardSuit.type]