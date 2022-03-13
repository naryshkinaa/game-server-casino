package com.example.domain

import com.example.domain.CardRank.CardRank
import com.example.domain.CardSuit.CardSuit
import com.example.domain.api.outcoming.ResponseTypeType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class Card(
                 @JsonScalaEnumeration(classOf[CardSuitType]) suit: CardSuit,
                 @JsonScalaEnumeration(classOf[CardSuitType]) rank: CardRank
               )
