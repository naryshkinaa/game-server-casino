package com.example.rest

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object HealthRoute {
  val healthRoute: Route =
    path("health") {
      get {
        complete("PONG!")
      }
    }
}
