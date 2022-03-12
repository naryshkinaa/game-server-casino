package com.example.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.example.domain.GameType.GameType
import com.example.domain.{ConnectRequest, GameType, StartGameRequest}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val connectRequest = jsonFormat1(ConnectRequest)
  implicit def enumFormat[T <: Enumeration](implicit enu: T): RootJsonFormat[T#Value] = {
    new RootJsonFormat[T#Value] {
      def write(obj: T#Value): JsValue = JsString(obj.toString)
      def read(json: JsValue): T#Value = {
        json match {
          case JsString(txt) => enu.withName(txt)
          case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
        }
      }
    }
  }
  implicit val gameType: RootJsonFormat[GameType] = enumFormat(GameType)

  implicit val startGameRequest = jsonFormat2(StartGameRequest)
}