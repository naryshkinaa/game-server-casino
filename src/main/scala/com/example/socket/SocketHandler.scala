package com.example.socket

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.ByteString
import com.example.domain.api.incoming._
import com.example.domain.api.outcoming.push.{GameResultNotification, GameStartedNotification, UserNotification}
import com.example.domain.api.outcoming.response.{ApiResponse, ErrorResponse, GameConnectedResponse, UserInfoResponse}
import com.example.service.lobby.MainLobbyActor
import com.example.socket.domain.{RequestType, ResponseType, WrappedRequest, WrappedResponse}
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
          mainLobby ! MainLobbyActor.Connect(parsed.player, Some(self))
        case _ => client ! prepareRequest(ErrorResponse("User NOT authenticated"))
      }
    case e@UserInfoResponse(playerId, _, _) =>
      context.become(authenticatedState(playerId, sender()))
      client ! prepareRequest(e)

    case PeerClosed =>
      log.info("Server: PeerClosed")
      context.stop(self)
  }

  def authenticatedState(playerId: String, playerActor: ActorRef): Receive = {
    case Received(data) =>
      val wrapper = JsonUtil.fromJson[WrappedRequest](data.utf8String)
      wrapper.requestType match {
        case RequestType.Auth =>
          client ! prepareRequest(ErrorResponse("User already authenticated"))
        case RequestType.StartGame =>
          val parsed = JsonUtil.fromJson[StartGameRequest](wrapper.serializedBody)
          mainLobby ! MainLobbyActor.NewGame(playerId, parsed.gameType, None)
        case RequestType.GameAction =>
          val parsed = JsonUtil.fromJson[UserActionRequest](wrapper.serializedBody)
          playerActor ! parsed
      }

    case u: UserNotification =>
      client ! prepareRequest(u)

    case u: ApiResponse =>
      client ! prepareRequest(u)


    case PeerClosed =>
      println("Server: PeerClosed")
      context.stop(self)
  }

  private def prepareRequest(notification: UserNotification): Write = {
    val responseType = notification match {
      case _: GameResultNotification => ResponseType.GameResult
      case _: GameStartedNotification => ResponseType.GameStarted
    }
    Write(ByteString.apply(JsonUtil.toJson(WrappedResponse(responseType, JsonUtil.toJson(notification)))))
  }

  private def prepareRequest(response: ApiResponse): Write = {
    val responseType = response match {
      case _: ErrorResponse => ResponseType.Error
      case _: UserInfoResponse => ResponseType.AuthSuccess
      case _: GameConnectedResponse => ResponseType.GameConnected
    }
    Write(ByteString.apply(JsonUtil.toJson(WrappedResponse(responseType, JsonUtil.toJson(response)))))
  }

}
