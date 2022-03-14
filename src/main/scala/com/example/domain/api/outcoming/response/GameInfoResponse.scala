package com.example.domain.api.outcoming.response

import com.example.domain.game.Hand

case class GameInfoResponse(
                             gameId: String,
                             hand: Hand,
                             exist: Boolean,
                             actionDone: Boolean
                           ) extends ApiResponse
