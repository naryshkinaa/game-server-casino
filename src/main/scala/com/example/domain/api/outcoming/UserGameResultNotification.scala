package com.example.domain.api.outcoming

import com.example.domain.GameResult.GameResult
import com.example.domain.GameResultType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class UserGameResultNotification(
                           @JsonScalaEnumeration(classOf[GameResultType]) result: GameResult,
                           message: String,
                           balance: Int
                         ) extends UserNotification
