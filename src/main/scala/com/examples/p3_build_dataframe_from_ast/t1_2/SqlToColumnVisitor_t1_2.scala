package com.examples.p3_build_dataframe_from_ast.t1_2

import org.apache.calcite.sql._
import org.apache.calcite.sql.util.SqlVisitor
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.col

import scala.collection.JavaConverters.asScalaBufferConverter

class SqlToColumnVisitor_t1_2(context: Context_t1_2) extends SqlVisitor[List[Column]]{
  override def visit(literal: SqlLiteral): List[Column] = ???

  override def visit(call: SqlCall): List[Column] = ???

  override def visit(nodeList: SqlNodeList): List[Column] = {
    nodeList.asScala.flatMap(n => {
      n.accept(this)
    }).toList
  }

  override def visit(id: SqlIdentifier): List[Column] = {
    val names = id.names.asScala;

    names.length match {
      case 1 => {
        List(col(names.head))
      }
      case 2 => {
        val dfKey = names.head
        val colName = names(1)
        List(context.get(dfKey).col(colName))
      }
      case _ => throw new IllegalArgumentException("")
    }

  }

  override def visit(`type`: SqlDataTypeSpec): List[Column] = ???

  override def visit(param: SqlDynamicParam): List[Column] = ???

  override def visit(intervalQualifier: SqlIntervalQualifier): List[Column] = ???
}
