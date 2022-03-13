package com.example

import com.example.domain.{Card, CardRank, CardSuit, GameResult, Hand}
import com.example.service.compare.DoubleCardCompare
import org.scalatest.funspec.AnyFunSpec

class DoubleCardCompareTest extends AnyFunSpec {
  val compare = new DoubleCardCompare()
  it("A2 should be greater KK") {
    val result = compare.compare(
      Hand(Card(CardSuit.Diamond, CardRank.A) :: Card(CardSuit.Diamond, CardRank.`2`) :: Nil),
      Hand(Card(CardSuit.Diamond, CardRank.K) :: Card(CardSuit.Spades, CardRank.K) :: Nil),
    )
    assert(result == GameResult.Win)
  }

  it("A5 should be less AJ") {
    val result = compare.compare(
      Hand(Card(CardSuit.Diamond, CardRank.A) :: Card(CardSuit.Diamond, CardRank.`5`) :: Nil),
      Hand(Card(CardSuit.Spades, CardRank.A) :: Card(CardSuit.Spades, CardRank.J) :: Nil),
    )
    assert(result == GameResult.Lose)
  }

  it("Q5 should be equal Q5") {
    val result = compare.compare(
      Hand(Card(CardSuit.Diamond, CardRank.Q) :: Card(CardSuit.Diamond, CardRank.`5`) :: Nil),
      Hand(Card(CardSuit.Spades, CardRank.Q) :: Card(CardSuit.Spades, CardRank.`5`) :: Nil),
    )
    assert(result == GameResult.Equal)
  }

  it("66 should be equal 66") {
    val result = compare.compare(
      Hand(Card(CardSuit.Diamond, CardRank.`6`) :: Card(CardSuit.Clubs, CardRank.`6`) :: Nil),
      Hand(Card(CardSuit.Spades, CardRank.`6`) :: Card(CardSuit.Hearts, CardRank.`6`) :: Nil),
    )
    assert(result == GameResult.Equal)
  }
}


