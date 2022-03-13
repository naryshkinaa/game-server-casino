package com.example.config

import com.example.bot.strategy.BaseSingleCardStrategy

object Params {
  val timeLimitSec = 20
  val startBot = true
  val botStrategy = new BaseSingleCardStrategy()
  val botConcurrentGames = 2
  val restPort = 8080
  val socketPort = 8081

}
