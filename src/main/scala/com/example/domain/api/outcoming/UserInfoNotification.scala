package com.example.domain.api.outcoming

import com.example.domain.game.Hand

case class UserInfoNotification(
                                 balance: Int,
                                 activeGames: List[String]
                               ) extends UserNotification