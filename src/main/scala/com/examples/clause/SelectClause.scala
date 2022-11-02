package com.examples.clause

import com.examples.DataFrameBuilder
import org.apache.spark.sql.{Column, DataFrame}

case class SelectClause(df: DataFrame, dfb: DataFrameBuilder) {
  def apply(cols:List[Column]): DataFrame = {
    this.df.select(cols:_*);
  }
}
