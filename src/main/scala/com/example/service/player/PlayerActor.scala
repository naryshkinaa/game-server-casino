package com.example.service.player

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.example.domain.api.incoming.UserActionRequest
import com.example.domain.api.outcoming.push.{GameResultNotification, GameStartedNotification, UserNotification}
import com.example.domain.api.outcoming.response.{GameInfoResponse, GetEventsResponse, UserInfoResponse}
import com.example.domain.game.{GameResult, Hand, UserAction}
import com.example.service.games.AbstractTableGameActor

class PlayerActor(playerId: String, socketActor: ActorRef) extends Actor with ActorLogging {

  import PlayerActor._

  def receive = {
    mainState(PlayerActorContext(1000, Map(), Nil, Nil))
  }

  def mainState(state: PlayerActorContext): Receive = {
    //FROM server
    case Lose(gameId, amount, message) => {
      val newTokens = state.tokens - amount
      val newState = state.copy(tokens = newTokens, games = state.games - gameId)
      context.become(mainState(newState))
      notifyUser(GameResultNotification(gameId, GameResult.Lose, message, newTokens), newState)
    }
    case Win(gameId, amount, message) => {
      val newTokens = state.tokens + amount
      val newState = state.copy(tokens = newTokens, games = state.games - gameId)
      context.become(mainState(newState))
      notifyUser(GameResultNotification(gameId, GameResult.Win, message, newTokens), newState)
    }
    case GameStarted(gameId, hand, isRestarted) => notifyUser(GameStartedNotification(gameId, hand, isRestarted), state)
    case ConnectedToGame(gameId) =>
      context.become(mainState(state.copy(games = state.games + (gameId -> sender()))))

    case GetEvents(callback) => {
      callback ! GetEventsResponse(state.gameResults, state.gameStarted)
      context.become(mainState(state.copy(gameResults = Nil, gameStarted = Nil)))
    }

    case RestoreInfo(callback) =>
      callback ! UserInfoResponse(playerId, state.tokens, state.games.keys.toList)

    case RestoreGameInfo(gameId, callback) =>
      val gameActor = state.games.get(gameId)
      if (gameActor.isEmpty) callback ! GameInfoResponse(gameId, null, false, false)
      else gameActor.get ! AbstractTableGameActor.GetGameInfo(playerId, callback)

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
    socketActor ! notification
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

}

case class PlayerActorContext(
                               tokens: Int,
                               games: Map[String, ActorRef],
                               gameResults: List[GameResultNotification],
                               gameStarted: List[GameStartedNotification]
                             )