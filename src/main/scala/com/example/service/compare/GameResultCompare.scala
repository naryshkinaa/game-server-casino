package com.example.service.compare

import com.example.domain.game.GameResult.GameResult

trait GameResultCompare[A] {

  def compare(first: A, second: A): GameResult
}
