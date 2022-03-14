package com.example.domain.api.outcoming

import com.example.domain.game.GameResult.GameResult
import com.example.domain.game.GameResultType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class UserGameResultNotification(
                                       gameId: String,
                                       @JsonScalaEnumeration(classOf[GameResultType]) result: GameResult,
                                       message: String,
                                       balance: Int
                                     ) extends UserNotification
