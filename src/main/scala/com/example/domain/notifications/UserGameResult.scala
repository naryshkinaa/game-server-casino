package com.example.domain.notifications

import com.example.domain.GameResult.GameResult

case class UserGameResult(
                         result: GameResult,
                         message: String,
                         balance: Int
                         ) {

}
