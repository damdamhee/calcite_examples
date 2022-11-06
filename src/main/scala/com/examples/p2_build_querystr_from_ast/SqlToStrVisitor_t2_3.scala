package com.examples.p2_build_querystr_from_ast

import org.apache.calcite.sql._
import org.apache.calcite.sql.`type`.SqlTypeName
import org.apache.calcite.sql.util.SqlVisitor

import scala.collection.JavaConverters.asScalaBufferConverter

/*
SqlVisitor의 목적?
SqlVisitor 인터페이스는 AST 노드를 방문할 때마다 실행되어야 하는 로직을 정의한 Visitor 클래스이다.
SqlToStrVisitor 클래스는 AST 노드를 방문할 때마다 노드 -> QueryString으로 다시 변환하는 작업을 수행한다.
*/
class SqlToStrVisitor_t2_3 extends SqlVisitor[String] {
  /*
  아래 쿼리를 처리할 수 있는 Visitor를 구현한다.
SELECT col1, col2, col3
FROM TBL_1
WHERE col1 > 10 AND col2 > 100 OR col3 IS NOT NULL

root = {SqlSelect@1675} "SELECT `col1`, `col2`, `col3`\nFROM `TBL_1`\nWHERE `col1` > 10 AND `col2` > 100 OR `col3` IS NOT NULL"
 where = {SqlBasicCall@1697} "`col1` > 10 AND `col2` > 100 OR `col3` IS NOT NULL"
  operator = {SqlBinaryOperator@1788} "OR"
  operandList = {RegularImmutableList@1789}  size = 2
   0 = {SqlBasicCall@1794} "`col1` > 10 AND `col2` > 100"
    operator = {SqlBinaryOperator@1798} "AND"
    operandList = {RegularImmutableList@1799}  size = 2
   1 = {SqlBasicCall@1795} "`col3` IS NOT NULL"
    operator = {SqlPostfixOperator@1803} "IS NOT NULL"
    operandList = {SingletonImmutableList@1804}  size = 1
 selectList = {SqlNodeList@1695}  size = 3
 from = {SqlIdentifier@1695} "TBL_1"
   */
  val SPACE = " ";
  val COMMA = ", "
  val OPEN_BRACKET = "("
  val CLOSE_BRACKET = ")"

  val queryStr = scala.collection.mutable.ListBuffer[String]()
  override def visit(literal: SqlLiteral): String = {

    //아래와 같이 하면 무한루프임
    //literal.accept(this)
    literal.getTypeName match {
      case SqlTypeName.DECIMAL => String.valueOf(literal.getValue)
      //...타입 종류에 따라 다 구현해야할 듯
      case _ => ""
    }
  }

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

        val where = root.getWhere
        queryStr ++= List("WHERE", where.accept(this))

        //finalize
        queryStr.toList.mkString(SPACE)
      }
      //select
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
      //where condition exp
      case SqlKind.GREATER_THAN | SqlKind.AND | SqlKind.OR => {
       val root = call.asInstanceOf[SqlBasicCall]

        val OP = root.getOperator.getName
        val leftOperand = root.getOperandList.asScala.head.accept(this)
        val rightOperand = root.getOperandList.asScala(1).accept(this)
        List(
          leftOperand, OP, rightOperand
        ).mkString(SPACE)
      }
      case SqlKind.IS_NOT_NULL => {
        val root = call.asInstanceOf[SqlBasicCall]

        val POSTFIX_OP = root.getOperator.getName
        val leftOperand = root.getOperandList.asScala.head.accept(this)
        List(
          leftOperand, POSTFIX_OP
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
