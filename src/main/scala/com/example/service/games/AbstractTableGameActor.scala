package com.example.service.games

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}
import com.example.domain.api.outcoming.response
import com.example.domain.api.outcoming.response.{GameConnectedResponse, GameInfoResponse}
import com.example.domain.game.PlayerActionType.{FOLD, PLAY, PlayerActionType}
import com.example.domain.game._
import com.example.service.compare.HandCompare
import com.example.service.lobby.GameLobbyActor
import com.example.service.player.PlayerActor
import com.example.service.player.PlayerActor.{Lose, Win}

trait AbstractTableGameActor extends Actor with ActorLogging {

  import AbstractTableGameActor._

  def gameId: String

  def gameLobby: ActorRef

  def tableSize: Int

  def compare: HandCompare

  def fold_fold: Int

  def fold_play: Int

  def play_play: Int

  def startGameState(players: Map[String, ActorRef], isRestarted: Boolean): Receive

  def waitingState(players: Map[String, ActorRef]): Receive = {
    case PlayerJoin(playerId, player, callback) =>
      if (!players.contains(playerId)) {
        player ! PlayerActor.ConnectedToGame(gameId)
        callback ! GameConnectedResponse(gameId)
        if (players.size == tableSize - 1) {
          context.become(startGameState(players + (playerId -> player), false))
          gameLobby ! GameLobbyActor.GameStart(gameId, self)
        } else {
          context.become(waitingState(players + (playerId -> player)))
        }
      }
      else {
        gameLobby ! GameLobbyActor.NewGame(playerId, player, callback)
      }
    case PlayerExit(playerId) =>
      context.become(waitingState(players - playerId))

    case AbstractTableGameActor.GetGameInfo(_, callback) => callback ! response.GameInfoResponse(gameId, null, true, false)
  }

  def gameState(
                 players: Map[String, ActorRef],
                 playerHands: Map[String, Hand],
                 playersAction: Map[String, PlayerActionType]
               ): Receive = {
    case AutoFoldAction(playerId) =>
      processAction(playerId, PlayerActionType.FOLD, players, playerHands, playersAction)
    case UserAction(playerId, action) =>
      processAction(playerId, action, players, playerHands, playersAction)
    case PlayerExit(playerId) =>
      self ! AutoFoldAction(playerId)
    case AbstractTableGameActor.GetGameInfo(playerId, callback) => callback ! response.GameInfoResponse(gameId, playerHands(playerId), true, playersAction.contains(playerId))
  }

  private def processAction(
                             playerId: String,
                             actionType: PlayerActionType,
                             players: Map[String, ActorRef],
                             playerHands: Map[String, Hand],
                             playersAction: Map[String, PlayerActionType]): Unit = {
    if (!playersAction.contains(playerId)) {
      val updatedActions = playersAction + (playerId -> actionType)
      if (players.size == updatedActions.size) resolveGameResults(players, playerHands, updatedActions)
      else context.become(gameState(players, playerHands, updatedActions))
    }
  }

  private def resolveGameResults(
                                  players: Map[String, ActorRef],
                                  playerHands: Map[String, Hand],
                                  playersAction: Map[String, PlayerActionType]
                                ): Unit = {

    val player1 = players.keys.head
    val player2 = players.keys.toList(1)
    (playersAction(player1), playersAction(player2)) match {
      case (FOLD, FOLD) =>
        players(player1) ! Lose(gameId, fold_fold, s"Both players FOLD, LOSE $fold_fold token")
        players(player2) ! Lose(gameId, fold_fold, s"Both players FOLD, LOSE $fold_fold token")
        self ! PoisonPill
      case (FOLD, PLAY) =>
        players(player1) ! Lose(gameId, fold_play, s"You FOLD, opponent PLAY, LOSE $fold_play token")
        players(player2) ! Win(gameId, fold_play, s"You PLAY, opponent FOLD, WIN $fold_play token")
        self ! PoisonPill
      case (PLAY, FOLD) =>
        players(player1) ! Win(gameId, fold_play, s"You PLAY, opponent FOLD, WIN $fold_play token")
        players(player2) ! Lose(gameId, fold_play, s"You FOLD, opponent PLAY, LOSE $fold_play token")
        self ! PoisonPill
      case (PLAY, PLAY) =>
        compare.compare(playerHands(player1), playerHands(player2)) match {
          case GameResult.Win =>
            players(player1) ! Win(gameId, play_play, s"Both players PLAY, WIN $play_play token. \n Showdown: you ${playerHands(player1)}, op ${playerHands(player2)}")
            players(player2) ! Lose(gameId, play_play, s"Both players PLAY, LOSE $play_play token. \n Showdown: you ${playerHands(player2)}, op ${playerHands(player1)}")
            self ! PoisonPill
          case GameResult.Lose =>
            players(player1) ! Lose(gameId, play_play, s"Both players PLAY, LOSE $play_play token. \n Showdown: you ${playerHands(player1)}, op ${playerHands(player2)}")
            players(player2) ! Win(gameId, play_play, s"Both players PLAY, WIN $play_play token. \n Showdown: you ${playerHands(player2)}, op ${playerHands(player1)}")
            self ! PoisonPill
          case GameResult.Equal =>
            context.become(startGameState(players, true))
        }
    }

  }

  def receive = {
    waitingState(Map())
  }
}

object AbstractTableGameActor {

  case class PlayerJoin(playerId: String, player: ActorRef, callback: ActorRef)

  case class PlayerExit(playerId: String)

  case class GetGameInfo(playerId: String, callback: ActorRef)

}