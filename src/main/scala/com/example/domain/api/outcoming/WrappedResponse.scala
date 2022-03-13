package com.example.domain.api.outcoming

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class WrappedResponse(
                            @JsonScalaEnumeration(classOf[ResponseTypeType]) responseType: ResponseType.Value,
                            serializedBody: String
                          )
