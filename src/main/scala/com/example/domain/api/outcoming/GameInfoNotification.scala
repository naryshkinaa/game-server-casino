package com.example.domain.api.outcoming

import com.example.domain.game.Hand

case class GameInfoNotification(
                               gameId: String,
                               hand: Hand,
                               exist: Boolean,
                               actionDone: Boolean
                               ) {

}
