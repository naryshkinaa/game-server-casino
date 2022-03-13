package com.example.socket

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.ByteString
import com.example.domain.api.incoming._
import com.example.domain.api.outcoming.{ErrorNotification, ResponseType, UserInfoNotification, WrappedResponse}
import com.example.domain.game.MainLobbyEvents
import com.example.domain.{Hand, UserPush}
import com.example.socket.SocketHandler.SuccessAuth
import com.example.util.JsonUtil

class SocketHandler(mainLobby: ActorRef, client: ActorRef) extends Actor with ActorLogging {

  import akka.io.Tcp._

  def receive: Receive = {
    connectingState
  }

  def connectingState: Receive = {
    case Received(data) =>
      val wrapper = JsonUtil.fromJson[WrappedRequest](data.utf8String)
      wrapper.requestType match {
        case RequestType.Auth =>
          val parsed = JsonUtil.fromJson[AuthRequest](wrapper.serializedBody)
          mainLobby ! MainLobbyEvents.Connect(parsed.player)
        case _ => client ! prepareRequest(ResponseType.Error, ErrorNotification("User NOT authenticated"))
      }
    case SuccessAuth(playerId, playerActor, balance, games) =>
      context.become(authState(playerId, playerActor))
      client ! prepareRequest(ResponseType.AuthSuccess, UserInfoNotification(balance, games))

    case PeerClosed =>
      log.info("Server: PeerClosed ")
      context.stop(self)
  }

  def prepareRequest(responseType: ResponseType.Value, body: AnyRef): Write = {
    Write(ByteString.apply(JsonUtil.toJson(WrappedResponse(responseType, JsonUtil.toJson(body)))))
  }


  def authState(playerId: String, playerActor: ActorRef): Receive = {
    case Received(data) =>
      //      println("Server2: received " + data.utf8String)
      val wrapper = JsonUtil.fromJson[WrappedRequest](data.utf8String)
      wrapper.requestType match {
        case RequestType.Auth =>
          client ! prepareRequest(ResponseType.Error, ErrorNotification("User already authenticated"))
        case RequestType.StartGame =>
          val parsed = JsonUtil.fromJson[StartGameRequest](wrapper.serializedBody)
          mainLobby ! MainLobbyEvents.NewGame(playerId, parsed.gameType, None)
        case RequestType.GameAction =>
          val parsed = JsonUtil.fromJson[UserActionRequest](wrapper.serializedBody)
          playerActor ! parsed
      }

    case UserPush(responseType, body) =>
      client ! prepareRequest(responseType, body)


    case PeerClosed =>
      println("Server: PeerClosed")
      context.stop(self)
  }
}


object SocketHandler {

  case class SuccessAuth(
                          playerId: String,
                          playerActor: ActorRef,
                          balance: Int,
                          activeGames: Map[String, Hand]
                        )

}