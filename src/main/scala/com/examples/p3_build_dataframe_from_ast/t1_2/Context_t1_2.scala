package com.examples.p3_build_dataframe_from_ast.t1_2

import com.examples.p3_build_dataframe_from_ast.NotExistException
import org.apache.spark.sql.DataFrame

class Context_t1_2(sources:Map[String, DataFrame], var aliases: Map[String, DataFrame]) {
  def +(key:String, value:DataFrame):Context_t1_2 = {
    val map = scala.collection.mutable.Map[String, DataFrame]()
    map ++= this.sources
    map += (key -> value)
    new Context_t1_2(map.toMap, this.aliases)
  }

  def alias(key:String, value:DataFrame) = {
    val map = scala.collection.mutable.Map[String, DataFrame]()
    map ++= this.aliases
    map += (key -> value)
    this.aliases = map.toMap
  }

  def get(key:String): DataFrame = {
    this.sources.get(key) match {
      case Some(value) => value
      case None => aliases.get(key) match {
        case Some(value) => value
        case None => throw new NotExistException(s"table ${key} does not exist")
      }
    }
  }

}
