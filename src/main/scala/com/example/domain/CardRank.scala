package com.example.domain

import com.fasterxml.jackson.core.`type`.TypeReference

object CardRank extends Enumeration {
  type CardRank = Value
  val Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace = Value
}

class CardRankType extends TypeReference[CardRank.type]

