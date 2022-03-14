package com.example.socket.domain

import com.fasterxml.jackson.core.`type`.TypeReference

object ResponseType extends Enumeration {
  type ResponseType = Value
  val AuthSuccess, GameStarted, GameConnected, GameResult, Error = Value

}

class ResponseTypeType extends TypeReference[ResponseType.type]