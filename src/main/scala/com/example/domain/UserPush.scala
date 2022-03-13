package com.example.domain

import com.example.domain.api.outcoming.ResponseType

case class UserPush(responseType: ResponseType.Value, body: AnyRef)