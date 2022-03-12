package com.example.domain

case class Deck() {
  var cards = Deck.standardCards.toList

  def nextCard(): Card = {
    val n = util.Random.nextInt(cards.size)
    val card = cards(n)
    cards = cards.patch(n, Nil, 1)
    card
  }

}

object Deck {
  val standardCards: Set[Card] =
    for (
      suit <- CardSuit.values;
      rank <- CardRank.values
    ) yield Card(suit, rank)
}
