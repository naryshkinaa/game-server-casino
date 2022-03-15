package com.example.service.games

import akka.actor.ActorRef
import com.example.config.Params
import com.example.domain.game.{AutoFoldAction, Deck, Hand}
import com.example.service.compare.{HandCompare, OneCardCompare}
import com.example.service.player.PlayerActor.GameStarted

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class SingleCardGameActor(
                           val gameId: String,
                           val gameLobby: ActorRef
                         ) extends AbstractTableGameActor {
  val tableSize: Int = 2
  val timeLimitSec: Int = Params.timeActionLimitSec
  val compare: HandCompare = new OneCardCompare()
  val fold_fold: Int = 1
  val fold_play: Int = 3
  val play_play: Int = 10

  def startGameState(players: Map[String, ActorRef], isRestarted: Boolean): Receive = {
    val deck = Deck()
    val playerHands: Map[String, Hand] = players
      .keys
      .map(
        playerId => {
          val hand = Hand(deck.nextCard() :: Nil)
          players(playerId) ! GameStarted(gameId, hand, isRestarted)
          playerId -> hand
        }
      )
      .toMap

    players
      .keys
      .foreach(
        playerId => context.system.scheduler.scheduleOnce(
          timeLimitSec seconds,
          self,
          AutoFoldAction(playerId)
        )
      )
    gameState(players, playerHands, Map())
  }

}

