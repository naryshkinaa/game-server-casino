package com.example.strategy

import com.example.domain.{CardRank, Hand, PlayerActionType}
import com.example.domain.PlayerActionType.PlayerActionType


trait CardStrategy {
  def action(hand: Hand): PlayerActionType
}

class AgroSingleCardStrategy extends CardStrategy {
  def action(hand: Hand): PlayerActionType = {
    PlayerActionType.PLAY
  }
}

class BaseSingleCardStrategy extends CardStrategy {
  def action(hand: Hand): PlayerActionType = {
    if(hand.cards.size != 1) throw new RuntimeException("Error in game. Hand size is incorrect")
    if(hand.cards.head.rank >= CardRank.Eight) PlayerActionType.PLAY
    else PlayerActionType.FOLD
  }
}