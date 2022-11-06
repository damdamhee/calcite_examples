package com.examples.p3_build_dataframe_from_ast.t1_1

import com.examples.p3_build_dataframe_from_ast.NotExistException
import org.apache.spark.sql.DataFrame

class Context_t1_1(sources:Map[String, DataFrame]) {
  def +(key:String, value:DataFrame):Context_t1_1 = {
    val map = scala.collection.mutable.Map[String, DataFrame]()
    map ++= this.sources
    map += (key -> value)
    new Context_t1_1(map.toMap)
  }

  def get(key:String): DataFrame = {
    this.sources.get(key) match {
      case Some(value) => value
      case None => throw new NotExistException(s"table ${key} does not exist")
    }
  }
}
