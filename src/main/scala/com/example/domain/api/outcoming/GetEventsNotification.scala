package com.example.domain.api.outcoming

case class GetEventsNotification(
                                  gameResult: List[UserGameResultNotification],
                                  gameStarted: List[GameStartedNotification]
                                ) {

}
