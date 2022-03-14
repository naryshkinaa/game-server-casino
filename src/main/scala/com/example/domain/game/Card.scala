package com.example.domain.game

import com.example.domain.game.CardRank.CardRank
import com.example.domain.game.CardSuit.CardSuit
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class Card(
                 @JsonScalaEnumeration(classOf[CardSuitType]) suit: CardSuit,
                 @JsonScalaEnumeration(classOf[CardRankType]) rank: CardRank
               )
