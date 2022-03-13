package com.example.domain.api.outcoming

import com.example.domain.Hand

case class GameStartedNotification(gameId: String, hand: Hand, isRestart: Boolean) extends UserNotification