package com.example.service.lobby

import akka.actor.{Actor, ActorRef, Props}
import com.example.domain.game.GameType.GameType
import com.example.domain.game.PlayerActionType.PlayerActionType
import com.example.domain.api.incoming.UserActionRequest
import com.example.domain.game.GameType
import com.example.service.player.PlayerActor
import com.example.service.player.PlayerActor.{RestoreGameInfo, RestoreInfo}

import scala.collection.mutable

class MainLobbyActor(
                      singleCardGameLobby: ActorRef,
                      doubleCardGameLobby: ActorRef,
                    ) extends Actor {
  val onlinePlayers: mutable.Map[String, ActorRef] = collection.mutable.Map[String, ActorRef]()

  def receive: Receive = {
    case MainLobbyActor.NewGame(playerId, gameType, callback) =>
      val player = onlinePlayers.get(playerId)
      if (player.isEmpty) sender() ! "Player not connected"
      else {
        gameType match {
          case GameType.SINGLE_CARD_GAME => singleCardGameLobby ! GameLobbyActor.NewGame(playerId, player.get, callback.getOrElse(sender()))
          case GameType.DOUBLE_CARD_GAME => doubleCardGameLobby ! GameLobbyActor.NewGame(playerId, player.get, callback.getOrElse(sender()))
        }
      }

    case MainLobbyActor.Connect(playerId) =>
      val playerActor = onlinePlayers.getOrElse(playerId, {
        val newActor = context.actorOf(Props(classOf[PlayerActor], playerId, sender()))
        onlinePlayers.put(playerId, newActor)
        newActor
      })
      playerActor ! RestoreInfo(sender())

    case MainLobbyActor.GetGameInfo(playerId, gameId) =>
      val player = onlinePlayers.get(playerId)
      if (player.isEmpty) sender() ! "Player not connected"
      player.get ! RestoreGameInfo(gameId, sender())


    case MainLobbyActor.GetEvents(playerId) => {
      val player = onlinePlayers.get(playerId)
      if (player.isEmpty) sender() ! "Player not connected"
      else player.get ! PlayerActor.GetEvents(sender())
    }

    case MainLobbyActor.UserAction(playerId, gameId, action) => {
      val player = onlinePlayers.get(playerId)
      if (player.isEmpty) sender() ! "Player not connected"
      else player.get ! UserActionRequest(gameId, action)
    }
  }


}

object MainLobbyActor {

  case class NewGame(playerId: String, gameType: GameType, callback: Option[ActorRef])

  case class Connect(playerId: String)

  case class Disconnect(playerId: String)

  //tmp solution for rest UI
  case class GetEvents(playerId: String)

  case class UserAction(
                         playerId: String,
                         gameId: String,
                         action: PlayerActionType
                       )

  case class GetGameInfo(playerId: String, gameId: String)

}