package com.example.domain

import com.example.domain.GameType.GameType

case class StartGameRequest(
                             player: String,
                             gameType: GameType
                           ) {
}
