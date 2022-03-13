package com.example.domain.api.outcoming

import com.fasterxml.jackson.core.`type`.TypeReference

object ResponseType extends Enumeration {
  type ResponseType = Value
  val AuthSuccess, GameStarted, GameResult, Error = Value

}

class ResponseTypeType extends TypeReference[ResponseType.type]