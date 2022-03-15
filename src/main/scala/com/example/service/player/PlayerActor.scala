package com.example.service.player

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}
import com.example.config.Params
import com.example.domain.api.incoming.UserActionRequest
import com.example.domain.api.outcoming.push.{GameResultNotification, GameStartedNotification, UserNotification}
import com.example.domain.api.outcoming.response.{GameInfoResponse, GetEventsResponse, UserInfoResponse}
import com.example.domain.game.{GameResult, Hand, UserAction}
import com.example.service.games.AbstractTableGameActor
import com.example.service.lobby.MainLobbyActor.{Disconnect, UpdateBalance}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class PlayerActor(playerId: String, currentTokens: Int, socketActor: Option[ActorRef]) extends Actor with ActorLogging {

  import PlayerActor._

  def receive = {

    context.system.scheduler.scheduleOnce(
      Params.inactiveTimeDisconnectSec seconds,
      self,
      CheckActive
    )
    mainState(PlayerActorContext(currentTokens, System.currentTimeMillis(), Map(), Nil, Nil))
  }

  def mainState(state: PlayerActorContext): Receive = {
    //FROM server
    case Lose(gameId, amount, message) => {
      val newTokens = state.tokens - amount
      context.parent ! UpdateBalance(playerId, newTokens)
      val newState = state.copy(tokens = newTokens, games = state.games - gameId)
      context.become(mainState(newState))
      notifyUser(GameResultNotification(gameId, GameResult.Lose, message, newTokens), newState)
    }
    case Win(gameId, amount, message) => {
      val newTokens = state.tokens + amount
      context.parent ! UpdateBalance(playerId, newTokens)
      val newState = state.copy(tokens = newTokens, games = state.games - gameId)
      context.become(mainState(newState))
      notifyUser(GameResultNotification(gameId, GameResult.Win, message, newTokens), newState)
    }
    case GameStarted(gameId, hand, isRestarted) => notifyUser(GameStartedNotification(gameId, hand, isRestarted), state)
    case ConnectedToGame(gameId) =>
      context.become(mainState(state.copy(lastActive = System.currentTimeMillis(), games = state.games + (gameId -> sender()))))

    case GetEvents(callback) => {
      callback ! GetEventsResponse(state.gameResults, state.gameStarted)
      context.become(mainState(state.copy(lastActive = System.currentTimeMillis(), gameResults = Nil, gameStarted = Nil)))
    }

    case RestoreInfo(callback) =>
      callback ! UserInfoResponse(playerId, state.tokens, state.games.keys.toList)

    case RestoreGameInfo(gameId, callback) =>
      val gameActor = state.games.get(gameId)
      if (gameActor.isEmpty) callback ! GameInfoResponse(gameId, null, false, false)
      else gameActor.get ! AbstractTableGameActor.GetGameInfo(playerId, callback)

    case CheckActive =>
      if (state.games.isEmpty && socketActor.isEmpty && System.currentTimeMillis() - state.lastActive > Params.inactiveTimeDisconnectSec * 1000) {
        log.info("Stop player actor")
        context.parent ! Disconnect(playerId)
        self ! PoisonPill
      } else {
        context.system.scheduler.scheduleOnce(
          Params.inactiveTimeDisconnectSec seconds,
          self,
          CheckActive
        )
      }


    //FROM user
    case UserActionRequest(gameId, action) => state.games.get(gameId).foreach(g => g ! UserAction(playerId, action))
  }

  private def notifyUser(
                          notification: UserNotification,
                          state: PlayerActorContext
                        ): Unit = {
    notification match {
      case e: GameStartedNotification => context.become(mainState(state.copy(gameStarted = state.gameStarted :+ e)))
      case e: GameResultNotification => context.become(mainState(state.copy(gameResults = state.gameResults :+ e)))
    }
    socketActor.foreach(_ ! notification)
  }

}

object PlayerActor {

  case class Lose(gameId: String, amount: Int, message: String)

  case class Win(gameId: String, amount: Int, message: String)

  case class GameStarted(gameId: String, hand: Hand, isRestarted: Boolean)

  case class ConnectedToGame(gameId: String)

  case class GetEvents(callback: ActorRef)

  case class RestoreInfo(callback: ActorRef)

  case class RestoreGameInfo(gameId: String, callback: ActorRef)

  case object CheckActive

}

case class PlayerActorContext(
                               tokens: Int,
                               lastActive: Long,
                               games: Map[String, ActorRef],
                               gameResults: List[GameResultNotification],
                               gameStarted: List[GameStartedNotification]
                             )