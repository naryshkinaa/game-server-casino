package com.example.domain.api.outcoming

import com.example.domain.Hand

case class GameInfoNotification(
                               gameId: String,
                               hand: Hand,
                               exist: Boolean,
                               actionDone: Boolean
                               ) {

}
