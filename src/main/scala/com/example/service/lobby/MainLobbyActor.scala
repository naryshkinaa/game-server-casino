package com.example.service.lobby

import akka.actor.{Actor, ActorRef, Props}
import com.example.domain.GameType
import com.example.domain.api.incoming.UserActionRequest
import com.example.domain.game.GameLobbyEvents
import com.example.service.player.PlayerActor.{RestoreGameInfo, RestoreInfo}
import com.example.service.player.PlayerActor

import scala.collection.mutable

class MainLobbyActor(
                      singleCardGameLobby: ActorRef,
                      doubleCardGameLobby: ActorRef,
                    ) extends Actor {
  val onlinePlayers: mutable.Map[String, ActorRef] = collection.mutable.Map[String, ActorRef]()

  import com.example.domain.game.MainLobbyEvents._

  def receive: Receive = {
    case NewGame(playerId, gameType, callback) =>
      val player = onlinePlayers.get(playerId)
      if (player.isEmpty) sender() ! "Player not connected"
      else {
        gameType match {
          case GameType.SINGLE_CARD_GAME => singleCardGameLobby ! GameLobbyEvents.NewGame(playerId, player.get, callback.getOrElse(sender()))
          case GameType.DOUBLE_CARD_GAME => doubleCardGameLobby ! GameLobbyEvents.NewGame(playerId, player.get, callback.getOrElse(sender()))
        }
      }

    case Connect(playerId) =>
      val playerActor = onlinePlayers.getOrElse(playerId, {
        val newActor = context.actorOf(Props(classOf[PlayerActor], playerId, sender()))
        onlinePlayers.put(playerId, newActor)
        newActor
      })
      playerActor ! RestoreInfo(sender())

    case GetGameInfo(playerId, gameId) =>
      val player = onlinePlayers.get(playerId)
      if (player.isEmpty) sender() ! "Player not connected"
      player.get ! RestoreGameInfo(gameId, sender())


    case GetEvents(playerId) => {
      val player = onlinePlayers.get(playerId)
      if (player.isEmpty) sender() ! "Player not connected"
      else player.get ! PlayerActor.GetEvents(sender())
    }

    case UserActionWithPlayer(playerId, gameId, action) => {
      val player = onlinePlayers.get(playerId)
      if (player.isEmpty) sender() ! "Player not connected"
      else player.get ! UserActionRequest(gameId, action)
    }
  }


}

