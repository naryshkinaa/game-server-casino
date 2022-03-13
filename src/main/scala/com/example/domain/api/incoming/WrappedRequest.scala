package com.example.domain.api.incoming

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class WrappedRequest(
                           @JsonScalaEnumeration(classOf[RequestTypeType]) requestType: RequestType.Value,
                           serializedBody: String
                         )
