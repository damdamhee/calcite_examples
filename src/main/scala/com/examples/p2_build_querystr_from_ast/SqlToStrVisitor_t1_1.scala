package com.examples.p2_build_querystr_from_ast

import org.apache.calcite.sql.{SqlCall, SqlDataTypeSpec, SqlDynamicParam, SqlIdentifier, SqlIntervalQualifier, SqlKind, SqlLiteral, SqlNodeList, SqlSelect}
import org.apache.calcite.sql.util.SqlVisitor

import scala.collection.JavaConverters.asScalaBufferConverter

/*
SqlVisitor의 목적?
SqlVisitor 인터페이스는 AST 노드를 방문할 때마다 실행되어야 하는 로직을 정의한 Visitor 클래스이다.
SqlToStrVisitor 클래스는 AST 노드를 방문할 때마다 노드 -> QueryString으로 다시 변환하는 작업을 수행한다.
*/
class SqlToStrVisitor_t1_1 extends SqlVisitor[String] {
  /*
  아래 쿼리를 처리할 수 있는 Visitor를 구현한다.
   selectList = {SqlNodeList@1676}  size = 3
    0 = {SqlIdentifier@1772} "col1"
    1 = {SqlIdentifier@1773} "col2"
    2 = {SqlIdentifier@1774} "col3"
   from = {SqlIdentifier@1677} "TBL_1"
   */
  val SPACE = " ";

  val queryStr = scala.collection.mutable.ListBuffer[String]()
  override def visit(literal: SqlLiteral): String = ???

  override def visit(call: SqlCall): String = {
    call.getKind match {
      case SqlKind.SELECT => {
        val root = call.asInstanceOf[SqlSelect]

        //select절 처리
        val selectList = root.getSelectList
        queryStr ++= List("SELECT", selectList.accept(this));

        //from절 처리
        val from = root.getFrom;
        queryStr ++=  List("FROM", from.accept(this));

        //finalize
        queryStr.toList.mkString(SPACE)
      }
    }
  }

  //SELECT.selectList
  override def visit(nodeList: SqlNodeList): String = {
    nodeList.getList.asScala.map(n => {
      n.accept(this);
    }).mkString(SPACE)
  }

  override def visit(id: SqlIdentifier): String = {
    //참고. SqlIdentifier.kind는 무조건 "SqlIdentifier"이다. 테이블명 / 컬럼명인지 구분할 수는 없다.
    id.names.asScala.mkString(".")
  }

  override def visit(`type`: SqlDataTypeSpec): String = ???

  override def visit(param: SqlDynamicParam): String = ???

  override def visit(intervalQualifier: SqlIntervalQualifier): String = ???
}
