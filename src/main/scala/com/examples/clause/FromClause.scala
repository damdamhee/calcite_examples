package com.examples.clause

import com.examples.DataFrameBuilder
import org.apache.calcite.sql.{SqlIdentifier, SqlNode}
import org.apache.spark.sql.{Column, DataFrame}

case class FromClause(sqlNode:SqlNode,
                      datasources:scala.collection.mutable.Map[String, DataFrame]) {
  def apply(): DataFrame = {
    val id = sqlNode.asInstanceOf[SqlIdentifier];
    val dbTable = s"${id.names.get(0)}.${id.names.get(1)}"
    datasources.getOrElse(dbTable, throw new IllegalArgumentException)
  }
}
