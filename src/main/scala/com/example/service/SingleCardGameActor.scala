package com.example.service

import akka.actor.{ActorRef, PoisonPill}
import com.example.domain.PlayerActionType.{FOLD, PLAY, PlayerActionType}
import com.example.domain.{Deck, GameResult, Hand, PlayerActionType}
import com.example.service.GameEvents.{Exit, FoldPlayAction}
import com.example.service.PlayerActor.{GameStarted, Lose, Win}
import com.example.service.compare.{HandCompare, OneCardCompare}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class SingleCardGameActor(
                           val gameId: String,
                           val gameLobby: ActorRef
                         ) extends AbstractTableGameActor {
  val tableSize: Int = 2
  val timeLimitSec: Int = 15
  val compare: HandCompare = new OneCardCompare()

  def startGameState(players: Map[String, ActorRef]): Receive = {
    val deck = Deck()
    val playerHands: Map[String, Hand] = players
      .keys
      .map(
        playerId => {
          val hand = Hand(deck.nextCard() :: Nil)
          players(playerId) ! GameStarted(gameId, hand)
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
          FoldPlayAction(PlayerActionType.FOLD, playerId)
        )
      )
    gameState(players, playerHands, Map())
  }

  def gameState(
                 players: Map[String, ActorRef],
                 playerHands: Map[String, Hand],
                 playersAction: Map[String, PlayerActionType]
               ): Receive = {
    case GameEvents.FoldPlayAction(actionType, playerId) =>
      if (!playersAction.contains(playerId)) {
        val updatedActions = playersAction + (playerId -> actionType)
        if (players.size == updatedActions.size) resolveGameResults(players, playerHands, updatedActions)
        else context.become(gameState(players, playerHands, updatedActions))
      }
    case Exit(playerId: String, player: ActorRef) =>
      self ! FoldPlayAction(PlayerActionType.PLAY, playerId)
  }

  def resolveGameResults(
                          players: Map[String, ActorRef],
                          playerHands: Map[String, Hand],
                          playersAction: Map[String, PlayerActionType]
                        ): Unit = {
    val player1 = players.keys.head
    val player2 = players.keys.toList(1)
    (playersAction(player1), playersAction(player2)) match {
      case (FOLD, FOLD) =>
        players(player1) ! Lose(gameId, 1, "Both players FOLD, LOSE 1 token")
        players(player2) ! Lose(gameId, 1, "Both players FOLD, LOSE 1 token")
        self ! PoisonPill
      case (FOLD, PLAY) =>
        players(player1) ! Lose(gameId, 3, "You FOLD, opponent PLAY, LOSE 3 token")
        players(player2) ! Win(gameId, 3, "You PLAY, opponent FOLD, WIN 3 token")
        self ! PoisonPill
      case (PLAY, FOLD) =>
        players(player1) ! Win(gameId, 3, "You PLAY, opponent FOLD, WIN 3 token")
        players(player2) ! Lose(gameId, 3, "You FOLD, opponent PLAY, LOSE 3 token")
        self ! PoisonPill
      case (PLAY, PLAY) =>
        compare.compare(playerHands(player1), playerHands(player2)) match {
          case GameResult.Win =>
            players(player1) ! Win(gameId, 10, s"Both players PLAY, WIN 10 token. \n Showdown: you ${playerHands(player1)}, op ${playerHands(player2)}")
            players(player2) ! Lose(gameId, 10, s"Both players PLAY, LOSE 10 token. \n Showdown: you ${playerHands(player2)}, op ${playerHands(player1)}")
            self ! PoisonPill
          case GameResult.Lose =>
            players(player1) ! Lose(gameId, 10, s"Both players PLAY, LOSE 10 token. \n Showdown: you ${playerHands(player1)}, op ${playerHands(player2)}")
            players(player2) ! Win(gameId, 10, s"Both players PLAY, WIN 10 token. \n Showdown: you ${playerHands(player2)}, op ${playerHands(player1)}")
            self ! PoisonPill
          case GameResult.Equal =>
            context.become(startGameState(players))
        }
    }

  }

}

