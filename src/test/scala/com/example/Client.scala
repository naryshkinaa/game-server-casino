package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import com.example.domain.api.outcoming.{GameStartedNotification, ResponseType, UserGameResultNotification, UserInfoNotification, WrappedResponse}
import com.example.util.JsonUtil

import java.net.InetSocketAddress

object Client {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Client], remote, replies)
}

class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor with ActorLogging {

  import akka.io.Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)

  def receive = {
    case CommandFailed(_: Connect) =>
      log.error("Client CommandFailed")
      context.stop(self)

    case c @ Connected(remote, local) =>
      val connection = sender()
      connection ! Register(self)
      context.become {
        case data: ByteString =>
          connection ! Write(data)
          Thread.sleep(10)
        case CommandFailed(w: Write) =>
          log.error("Client CommandFailed")
          context.stop(self)
        case Received(data) =>
          val parsed = JsonUtil.fromJson[WrappedResponse](data.utf8String)
          parsed.responseType match {
            case ResponseType.AuthSuccess => context.parent ! JsonUtil.fromJson[UserInfoNotification](parsed.serializedBody)
            case ResponseType.GameStarted => context.parent ! JsonUtil.fromJson[GameStartedNotification](parsed.serializedBody)
            case ResponseType.GameResult => context.parent ! JsonUtil.fromJson[UserGameResultNotification](parsed.serializedBody)
          }
        case "close" =>
          log.info("Client: close")
          connection ! Close
        case _: ConnectionClosed =>
          log.info("Client: ConnectionClosed")
          context.stop(self)
      }
  }
}