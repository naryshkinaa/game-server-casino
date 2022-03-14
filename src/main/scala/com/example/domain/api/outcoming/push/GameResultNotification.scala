package com.example.domain.api.outcoming.push

import com.example.domain.game.GameResult.GameResult
import com.example.domain.game.GameResultType
import com.example.socket.domain.ResponseType
import com.example.socket.domain.ResponseType.ResponseType
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class GameResultNotification(
                                   gameId: String,
                                   @JsonScalaEnumeration(classOf[GameResultType]) result: GameResult,
                                   message: String,
                                   balance: Int
                                 ) extends UserNotification