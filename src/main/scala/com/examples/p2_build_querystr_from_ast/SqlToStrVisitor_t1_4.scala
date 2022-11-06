package com.examples.p2_build_querystr_from_ast

import org.apache.calcite.sql._
import org.apache.calcite.sql.util.SqlVisitor

import scala.collection.JavaConverters.asScalaBufferConverter

/*
SqlVisitor의 목적?
SqlVisitor 인터페이스는 AST 노드를 방문할 때마다 실행되어야 하는 로직을 정의한 Visitor 클래스이다.
SqlToStrVisitor 클래스는 AST 노드를 방문할 때마다 노드 -> QueryString으로 다시 변환하는 작업을 수행한다.
*/
class SqlToStrVisitor_t1_4 extends SqlVisitor[String] {
  /*
  아래 쿼리를 처리할 수 있는 Visitor를 구현한다.
  SELECT col1, LOWER(col2), CAST(col3 AS INT)
  FROM TBL_1

  root = {SqlSelect@1602} "SELECT `col1`, LOWER(`col2`), CAST(`col3` AS INTEGER)\nFROM `TBL_1`"
   selectList = {SqlNodeList@1684}  size = 3
    0 = {SqlIdentifier@1776} "col1"
    1 = {SqlBasicCall@1777} "LOWER(`col2`)"
    2 = {SqlBasicCall@1778} "CAST(`col3` AS INTEGER)"
   from = {SqlIdentifier@1685} "TBL_1"
   */
  val SPACE = " ";
  val COMMA = ", "
  val OPEN_BRACKET = "("
  val CLOSE_BRACKET = ")"

  val queryStr = scala.collection.mutable.ListBuffer[String]()
  override def visit(literal: SqlLiteral): String = ???

  override def visit(call: SqlCall): String = {
    val kind = call.getKind
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
      case SqlKind.AS => {
        val root = call.asInstanceOf[SqlBasicCall]

        val tableIdentifierNode = root.getOperandList.asScala.head
        val tableAliasIdentifierNode = root.getOperandList.asScala(1)
        List(
          tableIdentifierNode.accept(this),
          "AS",
          tableAliasIdentifierNode.accept(this)
        ).mkString(SPACE)
      }
      case SqlKind.CAST => {
        val root = call.asInstanceOf[SqlBasicCall]

        val CAST = root.getOperator.getName
        val targetColumn = root.getOperandList.asScala.head //SqlIdentifier
        val targetType = root.getOperandList.asScala(1) //SqlDataTypeSpec

        List(
          CAST, OPEN_BRACKET, targetColumn.accept(this), "AS", targetType.accept(this),CLOSE_BRACKET
        ).mkString(SPACE)
      }
      case SqlKind.OTHER_FUNCTION => {
        //Q.OTHER_FUNCTION에 속한 함수는 모두 단일 파라미터만 받는 함수인가?
        //Q.아마도 모든 함수에 대한 단위테스트가 필요해보인다.
        val root = call.asInstanceOf[SqlBasicCall]

        val FUNC = root.getOperator.getName;
        val targetColumn = root.getOperandList.asScala.head

        List(
          FUNC, OPEN_BRACKET, targetColumn.accept(this), CLOSE_BRACKET
        ).mkString(SPACE)

      }
    }
  }

  //SELECT.selectList
  override def visit(nodeList: SqlNodeList): String = {
    nodeList.getList.asScala.map(n => {
      n.accept(this);
    }).mkString(COMMA)
  }

  override def visit(id: SqlIdentifier): String = {
    //참고. SqlIdentifier.kind는 무조건 "SqlIdentifier"이다. 테이블명 / 컬럼명인지 구분할 수는 없다.
    id.names.asScala.mkString(".")
  }

  override def visit(`type`: SqlDataTypeSpec): String = {
    //getTypeName = SqlIdentifier
    `type`.getTypeName.accept(this)
  }

  override def visit(param: SqlDynamicParam): String = ???

  override def visit(intervalQualifier: SqlIntervalQualifier): String = ???
}
