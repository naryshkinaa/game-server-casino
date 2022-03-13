package com.example.socket

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}

import java.net.InetSocketAddress

class SocketServer(mainLobby: ActorRef) extends Actor {

  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 6000))

  def receive = {
    case Bound(localAddress) =>
      println("Server Bound")

    case CommandFailed(_: Bind) =>  {
      println("Server: CommandFailed")
      context.stop(self)
    }

    case Connected(remote, local) =>
      println("Server: new connection")
      val connection = sender()
      val handler = context.actorOf(Props(classOf[SocketHandler],mainLobby, connection))
      connection ! Register(handler)
  }

}
