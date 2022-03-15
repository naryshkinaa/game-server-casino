package com.example.service.lobby

import akka.actor.{Actor, ActorRef, Props}
import com.example.domain.api.incoming.UserActionRequest
import com.example.domain.api.outcoming.response.{ErrorResponse, GameInfoResponse, GetEventsResponse}
import com.example.domain.game.GameType
import com.example.domain.game.GameType.GameType
import com.example.domain.game.PlayerActionType.PlayerActionType
import com.example.service.lobby.MainLobbyActor.{Disconnect, UpdateBalance}
import com.example.service.player.PlayerActor
import com.example.service.player.PlayerActor.{RestoreGameInfo, RestoreInfo}
import org.mapdb.{DBMaker, HTreeMap, Serializer}

import scala.collection.mutable

class MainLobbyActor(
                      singleCardGameLobby: ActorRef,
                      doubleCardGameLobby: ActorRef,
                    ) extends Actor {

  val onlinePlayers: mutable.Map[String, ActorRef] = collection.mutable.Map[String, ActorRef]()

  def receive: Receive = {
    case MainLobbyActor.NewGame(playerId, gameType, callback) =>
      val playerActor = getOrCreatePlayerActor(playerId, None)
      gameType match {
        case GameType.SINGLE_CARD_GAME => singleCardGameLobby ! GameLobbyActor.NewGame(playerId, playerActor, callback.getOrElse(sender()))
        case GameType.DOUBLE_CARD_GAME => doubleCardGameLobby ! GameLobbyActor.NewGame(playerId, playerActor, callback.getOrElse(sender()))
      }

    case MainLobbyActor.Connect(playerId, socket) =>
      val playerActor = getOrCreatePlayerActor(playerId, socket)
      playerActor ! RestoreInfo(sender())

    case MainLobbyActor.GetGameInfo(playerId, gameId) =>
      val playerActor = getOrCreatePlayerActor(playerId, None)
      playerActor ! RestoreGameInfo(gameId, sender())


    case MainLobbyActor.GetEvents(playerId) => {
      val playerActor = getOrCreatePlayerActor(playerId, None)
      playerActor ! PlayerActor.GetEvents(sender())
    }

    case MainLobbyActor.UserAction(playerId, gameId, action) => {
      val playerActor = getOrCreatePlayerActor(playerId, None)
      playerActor ! UserActionRequest(gameId, action)
    }

    case Disconnect(playerId) => {
      onlinePlayers.remove(playerId)
    }

    case UpdateBalance(playerId, tokens) => {
      saveBalance(playerId, tokens)
    }
  }

  def getOrCreatePlayerActor(playerId: String, socket: Option[ActorRef]) = {
    onlinePlayers.getOrElse(playerId, {
      val tokens = loadBalance(playerId)
      val newActor = context.actorOf(Props(classOf[PlayerActor], playerId, tokens, socket))
      onlinePlayers.put(playerId, newActor)
      newActor
    })
  }

  private def loadBalance(playerId: String): Int = {
    val tokens: Int = withDB(accountsTable => Option(accountsTable.get(playerId)).map(_.intValue()).getOrElse(1000))
    tokens
  }

  private def saveBalance(playerId: String, tokens: Int) = {
    withDB(accountsTable => accountsTable.put(playerId, tokens))
  }

  private def withDB[A](f: HTreeMap[String, Integer] => A): A = {
    val db = DBMaker.fileDB("game-server.db").make()
    val accountsTable = db.hashMap("accounts")
      .keySerializer(Serializer.STRING)
      .valueSerializer(Serializer.INTEGER)
      .createOrOpen()
    val result =
      try {
        f(accountsTable)
      }
      finally {
        db.close()
      }
    result
  }

}

object MainLobbyActor {

  case class NewGame(playerId: String, gameType: GameType, callback: Option[ActorRef])

  case class Connect(playerId: String, socket: Option[ActorRef])

  case class Disconnect(playerId: String)

  //tmp solution for rest UI
  case class GetEvents(playerId: String)

  case class UserAction(
                         playerId: String,
                         gameId: String,
                         action: PlayerActionType
                       )

  case class GetGameInfo(playerId: String, gameId: String)

  case class UpdateBalance(playerId: String, tokens: Int)

}