package com.example.domain

import com.example.domain.api.outcoming.ResponseType
import com.fasterxml.jackson.core.`type`.TypeReference

object CardSuit extends Enumeration {
  type CardSuit = Value
  val Diamonds, Hearts, Clubs, Spades = Value
}

class CardSuitType extends TypeReference[CardSuit.type]