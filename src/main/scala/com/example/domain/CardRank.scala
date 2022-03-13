package com.example.domain

import com.fasterxml.jackson.core.`type`.TypeReference

object CardRank extends Enumeration {
  type CardRank = Value
  val `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, T, J, Q, K, A = Value
}

class CardRankType extends TypeReference[CardRank.type]

