package com.examples.p3_build_dataframe_from_ast.t1_2

import org.apache.calcite.sql._
import org.apache.calcite.sql.util.SqlVisitor
import org.apache.spark.sql.{Column, DataFrame}

import scala.collection.JavaConverters.asScalaBufferConverter

/*
* AS 구문 처리 시, DataFrame 정보를 담고 있는 Context에
  Alias정보도 담을 수 있도록 Context를 변경하였다.
* 추가적으로, AS구문을 처리할 수 있는 AsVisitor를 구현하였다.
  As 하나만을 위한 Visitor를 구현한게 좀 걸리지만 우선 이런식으로 쭉 추가하다가 추상화하자.
* Context_t1_2.alias 구현이 좀 아쉽다.
  Context_t1_2를 mutable하게 만든 것 같다.
 */
class SqlToDataFrameVisitor_t1_2(context:Context_t1_2) extends SqlVisitor[DataFrame]{
  override def visit(literal: SqlLiteral): DataFrame = ???

  override def visit(call: SqlCall): DataFrame = {
    call.getKind match {
      case SqlKind.SELECT => {
        val root = call.asInstanceOf[SqlSelect]

        val from:DataFrame = root.getFrom.accept(this)
        val selectList:List[Column] = root.getSelectList.accept(new SqlToColumnVisitor_t1_2(context))
        from.select(selectList:_*)
      }
      case SqlKind.AS => {
        val root = call.asInstanceOf[SqlBasicCall]
        val df = root.getOperandList.asScala.head.accept(this)
        val alias = root.getOperandList.asScala(1).accept(new AsVisitor)
        context.alias(alias, df)
        df
      }
    }
  }

  override def visit(nodeList: SqlNodeList): DataFrame = ???

  override def visit(id: SqlIdentifier): DataFrame = {
    val name = id.names.asScala.mkString(".")
    context.get(name)
  }

  override def visit(`type`: SqlDataTypeSpec): DataFrame = ???

  override def visit(param: SqlDynamicParam): DataFrame = ???

  override def visit(intervalQualifier: SqlIntervalQualifier): DataFrame = ???
}
