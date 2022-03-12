package com.example.service

import akka.actor.{Actor, ActorRef, Props}
import com.example.boot.Boot.{doubleGameActor, singleGameActor, system}
import com.example.domain.GameType.{DOUBLE_CARD_GAME, GameType}
import com.example.domain.{GameType}
import com.example.service.MainLobbyActor.NewGame

import scala.collection.mutable

class MainLobbyActor(
                      singleCardGameLobby: ActorRef,
                      doubleCardGameLobby: ActorRef,
                    ) extends Actor {
  val onlinePlayers: mutable.Map[String, ActorRef] = collection.mutable.Map[String, ActorRef]()
  import MainLobbyActor._

  def receive: Receive = {
    case NewGame(playerId, gameType) =>
      val player = onlinePlayers.get(playerId)
      if(player.isEmpty) sender() ! "Player not connected"
      else {
        gameType match {
          case GameType.SINGLE_CARD_GAME => singleCardGameLobby ! GameLobby.NewGame(playerId, player.get)
          case GameType.DOUBLE_CARD_GAME => doubleCardGameLobby ! GameLobby.NewGame(playerId, player.get)
        }
        sender() ! "Game is started"

      }

    case Connect(player) =>
      if(!onlinePlayers.contains(player)){
        val playerActor = context.actorOf(Props(classOf[PlayerActor]))
        onlinePlayers.put(player, playerActor)
      }
  }


}

object MainLobbyActor {

  sealed trait Message

  case class NewGame(playerId: String, gameType: GameType) extends Message

  case class Connect(playerId: String) extends Message

  case class Disconnect(playerId: String) extends Message

}