package com.examples.p3_build_dataframe_from_ast.t1_1

import org.apache.calcite.sql.util.SqlVisitor
import org.apache.calcite.sql._
import org.apache.spark.sql.{Column, DataFrame}

import scala.collection.JavaConverters.asScalaBufferConverter

/*
DataFrame을 만들 때 유념해야 할 점
* DataFrame을 갖는다
  => Context를 갖는다
  => 쿼리의 어느 부분에 접근하느냐에 따라 참조하는 Context가 달라져야 한다
 */

//처음에 전달되는 정보는 Source 정보들이 담겨있는 Context이다.
//SqlToStrVisitor에서 처럼 한 클래스 안에서 구현이 어려울 것 같다.
//왜냐하면 context를 재귀적으로 넘길 수가 없기 때문이다.
class SqlToDataFrameVisitor_t1_1(context:Context_t1_1) extends SqlVisitor[DataFrame]{
  val columnVisitor = new SqlToColumnVisitor_t1_1
  override def visit(literal: SqlLiteral): DataFrame = ???

  override def visit(call: SqlCall): DataFrame = {
    call.getKind match {
      case SqlKind.SELECT => {
        val root = call.asInstanceOf[SqlSelect]

        val from:DataFrame = root.getFrom.accept(this)
        val selectList:List[Column] = root.getSelectList.accept(columnVisitor)
        from.select(selectList:_*)
      }

    }
  }

  //이렇게 되면 각 컬럼마다 select하게 되는 오류가 발생한다.
  //즉, DataFrame.select(여러 컬럼)이 불가능하다.
  override def visit(nodeList: SqlNodeList): DataFrame = ???

  override def visit(id: SqlIdentifier): DataFrame = {
    //테이블명만 이곳으로 올 수 있어야 한다.
    val name = id.names.asScala.mkString(".")
    context.get(name)
  }

  override def visit(`type`: SqlDataTypeSpec): DataFrame = ???

  override def visit(param: SqlDynamicParam): DataFrame = ???

  override def visit(intervalQualifier: SqlIntervalQualifier): DataFrame = ???
}
