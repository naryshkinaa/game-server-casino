package com.example.domain

import com.example.domain.CardRank.CardRank
import com.example.domain.CardSuit.CardSuit

case class Card(
                       suit: CardSuit,
                       rank: CardRank
                     )
