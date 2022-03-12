package com.example.service.compare
import com.example.domain.GameResult.GameResult
import com.example.domain.{GameResult, Hand}

class OneCardCompare extends HandCompare {
  def compare(first: Hand, second: Hand): GameResult = {
    if(first.cards.size != 1) throw new RuntimeException("Error in logic: should be one card")
    if(second.cards.size != 1) throw new RuntimeException("Error in logic: should be one card")
    if(first.cards.head.rank > second.cards.head.rank) GameResult.Win
    else {
      if(first.cards.head.rank < second.cards.head.rank) GameResult.Lose
      else GameResult.Equal
    }
  }
}
