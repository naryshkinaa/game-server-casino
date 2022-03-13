package com.example.domain.api.outcoming

import com.example.domain.Hand

case class UserInfoNotification(
                                 balance: Int,
                                 activeGames: List[String]
                               ) extends UserNotification