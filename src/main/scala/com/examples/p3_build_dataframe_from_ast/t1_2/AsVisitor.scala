package com.examples.p3_build_dataframe_from_ast.t1_2

import org.apache.calcite.sql.util.SqlVisitor
import org.apache.calcite.sql._

import scala.collection.JavaConverters.asScalaBufferConverter

class AsVisitor extends SqlVisitor[String]{
  override def visit(literal: SqlLiteral): String = ???

  override def visit(call: SqlCall): String = ???

  override def visit(nodeList: SqlNodeList): String = ???

  override def visit(id: SqlIdentifier): String = {
    id.names.asScala.head
  }

  override def visit(`type`: SqlDataTypeSpec): String = ???

  override def visit(param: SqlDynamicParam): String = ???

  override def visit(intervalQualifier: SqlIntervalQualifier): String = ???
}
