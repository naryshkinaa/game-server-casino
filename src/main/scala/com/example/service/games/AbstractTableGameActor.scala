package com.example.service.games

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}
import com.example.domain.PlayerActionType.{FOLD, PLAY, PlayerActionType}
import com.example.domain.api.outcoming.GameInfoNotification
import com.example.domain.game.{AutoFoldAction, GameEvents, GameLobbyEvents, UserAction}
import com.example.domain.{GameResult, Hand, PlayerActionType}
import com.example.service.compare.HandCompare
import com.example.service.player.PlayerActor
import com.example.service.player.PlayerActor.{Lose, Win}

trait AbstractTableGameActor extends Actor with ActorLogging {

  import com.example.domain.game.GameEvents._

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
        player ! PlayerActor.ConnectedToGame(gameId, self)
        callback ! PlayerActor.ConnectedToGame(gameId, self)
        if (players.size == tableSize - 1) {
          context.become(startGameState(players + (playerId -> player), false))
          //note possible chance that new Player (at client) does not complete process Connected to Game message
          gameLobby ! GameLobbyEvents.GameStart(gameId, self)
        } else {
          context.become(waitingState(players + (playerId -> player)))
        }
      }
      else {
        gameLobby ! GameLobbyEvents.NewGame(playerId, player, callback)
      }
    case PlayerExit(playerId: String, player: ActorRef) =>
      context.become(waitingState(players - playerId))

    case GameEvents.GetGameInfo(playerId, callback) => callback ! GameInfoNotification(gameId, null, true, false)
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
    case PlayerExit(playerId: String, player: ActorRef) =>
      self ! AutoFoldAction(playerId)
    case GameEvents.GetGameInfo(playerId, callback) => callback ! GameInfoNotification(gameId, playerHands(playerId), true, playersAction.contains(playerId))
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
