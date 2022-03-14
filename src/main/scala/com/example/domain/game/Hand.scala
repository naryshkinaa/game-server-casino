package com.example.domain.game

case class Hand(cards: List[Card]) {
  override def toString: String = cards.map(c => s"${c.rank} ${c.suit}").mkString(", ")
}
