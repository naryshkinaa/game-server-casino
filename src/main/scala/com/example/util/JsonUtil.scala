package com.example.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper


object JsonUtil {

  private val mapperParser = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
    mapper
  }
  private val mapperWriter = {
    val mapper = new ObjectMapper()
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    mapper.registerModule(DefaultScalaModule)
    mapper
  }

  def toJson(value: Map[Symbol, Any]): String = toJson(value map { case (k, v) => k.name -> v })

  def toJson(value: Any): String = mapperWriter.writeValueAsString(value)

  def toMap[V](json: String)(implicit m: Manifest[V]) = fromJson[Map[String, V]](json)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = mapperParser.readValue[T](json)

}