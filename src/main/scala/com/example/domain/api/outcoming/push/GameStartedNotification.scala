package com.example.domain.api.outcoming.push

import com.example.domain.game.Hand

case class GameStartedNotification(
                                    gameId: String,
                                    hand: Hand,
                                    isRestart: Boolean
                                  ) extends UserNotification
