package com.example.domain.api.outcoming.response

import com.example.domain.api.outcoming.push.{GameResultNotification, GameStartedNotification}

case class GetEventsResponse(
                              gameResult: List[GameResultNotification],
                              gameStarted: List[GameStartedNotification]
                            ) extends ApiResponse
