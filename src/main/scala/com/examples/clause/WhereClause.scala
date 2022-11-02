package com.examples.clause

import com.examples.DataFrameBuilder
import org.apache.calcite.sql.{SqlBasicCall, SqlNode}
import org.apache.spark.sql.DataFrame

case class WhereClause(sqlNode:SqlNode, dfb:DataFrameBuilder) {
  def apply(df:DataFrame): DataFrame = {
    if(sqlNode != null)
      sqlNode.accept(dfb)
    df
  }
}
