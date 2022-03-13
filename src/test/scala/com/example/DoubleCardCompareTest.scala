package com.example

import com.example.domain.{Card, CardRank, CardSuit, GameResult, Hand}
import com.example.service.compare.DoubleCardCompare
import org.scalatest.funspec.AnyFunSpec

class DoubleCardCompareTest extends AnyFunSpec {
  val compare = new DoubleCardCompare()
  it("A2 should be greater KK") {
    val result = compare.compare(
      Hand(Card(CardSuit.Diamonds, CardRank.Ace) :: Card(CardSuit.Diamonds, CardRank.Two) :: Nil),
      Hand(Card(CardSuit.Diamonds, CardRank.King) :: Card(CardSuit.Spades, CardRank.King) :: Nil),
    )
    assert(result == GameResult.Win)
  }

  it("A5 should be less AJ") {
    val result = compare.compare(
      Hand(Card(CardSuit.Diamonds, CardRank.Ace) :: Card(CardSuit.Diamonds, CardRank.Five) :: Nil),
      Hand(Card(CardSuit.Spades, CardRank.Ace) :: Card(CardSuit.Spades, CardRank.Jack) :: Nil),
    )
    assert(result == GameResult.Lose)
  }

  it("Q5 should be equal Q5") {
    val result = compare.compare(
      Hand(Card(CardSuit.Diamonds, CardRank.Queen) :: Card(CardSuit.Diamonds, CardRank.Five) :: Nil),
      Hand(Card(CardSuit.Spades, CardRank.Queen) :: Card(CardSuit.Spades, CardRank.Five) :: Nil),
    )
    assert(result == GameResult.Equal)
  }

  it("66 should be equal 66") {
    val result = compare.compare(
      Hand(Card(CardSuit.Diamonds, CardRank.Six) :: Card(CardSuit.Clubs, CardRank.Six) :: Nil),
      Hand(Card(CardSuit.Spades, CardRank.Six) :: Card(CardSuit.Hearts, CardRank.Six) :: Nil),
    )
    assert(result == GameResult.Equal)
  }
}


