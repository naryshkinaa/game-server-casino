package com.example.socket.domain

import com.fasterxml.jackson.core.`type`.TypeReference

object RequestType extends Enumeration {
  type RequestType = Value
  val Auth, StartGame, GameAction = Value

}

class RequestTypeType extends TypeReference[RequestType.type]