package com.example.domain.api.incoming

case class UserActionRequest(
                              gameId: String,
                              serializedAction: String
                            )
