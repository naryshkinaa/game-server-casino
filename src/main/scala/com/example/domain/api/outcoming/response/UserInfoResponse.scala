package com.example.domain.api.outcoming.response

case class UserInfoResponse(
                             playerId: String,
                             balance: Int,
                             activeGames: List[String]
                           ) extends ApiResponse
