package com.example.service.compare

import com.example.domain.GameResult.GameResult

trait GameResultCompare[A] {

  def compare(first: A, second: A): GameResult
}
