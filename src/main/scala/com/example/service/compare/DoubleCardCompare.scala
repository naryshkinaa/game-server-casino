package com.example.service.compare

import com.example.domain.GameResult.GameResult
import com.example.domain.{GameResult, Hand}

class DoubleCardCompare extends HandCompare {
  def compare(first: Hand, second: Hand): GameResult = {
    if(first.cards.size != 2) throw new RuntimeException("Error in logic: should be one card")
    if(second.cards.size != 2) throw new RuntimeException("Error in logic: should be one card")

    val firstPlayer = first.cards.map(_.rank.id).sorted
    val secondPlayer = second.cards.map(_.rank.id).sorted
    val firstScore = (firstPlayer(1) + 1) * 13 + firstPlayer(0)
    val secondScore = (secondPlayer(1) + 1) * 13 + secondPlayer(0)
    if( firstScore > secondScore) GameResult.Win
    else {
      if(firstScore < secondScore) GameResult.Lose
      else GameResult.Equal
    }
  }
}
