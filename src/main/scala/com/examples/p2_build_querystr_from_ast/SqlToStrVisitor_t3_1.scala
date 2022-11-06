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
class SqlToStrVisitor_t3_1 extends SqlVisitor[String] {
  /*
  아래 쿼리를 처리할 수 있는 Visitor를 구현한다.
SELECT col1, col2, col3
FROM TBL_1 A JOIN TBL_2 B ON A.col1 = B.col1

root = {SqlSelect@1668} "SELECT `A`.`col1`, `B`.`col2`, `B`.`col3`\nFROM `TBL_1` AS `A`\nINNER JOIN `TBL_2` AS `B` ON `A`.`col1` = `B`.`col1`"
 selectList = {SqlNodeList@1699}  size = 3
  0 = {SqlIdentifier@1790} "A.col1"
  1 = {SqlIdentifier@1791} "B.col2"
  2 = {SqlIdentifier@1792} "B.col3"
 from = {SqlJoin@1700} "SELECT *\nFROM `TBL_1` AS `A`\nINNER JOIN `TBL_2` AS `B` ON `A`.`col1` = `B`.`col1`"
  left = {SqlBasicCall@1797} "`TBL_1` AS `A`"
   operator = {SqlAsOperator@1812} "AS"
   operandList = {RegularImmutableList@1813}  size = 2
  natural = {SqlLiteral@1798} "FALSE"
  joinType = {SqlLiteral@1799} "INNER"
  right = {SqlBasicCall@1800} "`TBL_2` AS `B`"
   operator = {SqlAsOperator@1812} "AS"
   operandList = {RegularImmutableList@1821}  size = 2
 conditionType = {SqlLiteral@1792} "ON"
 condition = {SqlBasicCall@1793} "`A`.`col1` = `B`.`col1`"
  operator = {SqlBinaryOperator@1803} "="
  operandList = {RegularImmutableList@1804}  size = 2
  functionQuantifier = null
  pos = {SqlParserPos@1805} "line 3, column 30"
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
        queryStr ++=  (Option(root.getFrom) match {
          case Some(value) => List("FROM", value.accept(this))
          case None => List.empty
        })

        //where절 처리
        queryStr ++= (Option(root.getWhere) match {
          case Some(value) => List("WHERE", value.accept(this))
          case None => List.empty
        })

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
      case SqlKind.JOIN => {
        val root = call.asInstanceOf[SqlJoin]

        val JOIN_TYPE = root.getJoinType.name()
        val JOIN = "JOIN"
        val ON = root.getConditionType.name()
        val condition = root.getCondition.accept(this)
        val leftOperand = root.getLeft.accept(this)
        val rightOperand = root.getRight.accept(this)

        List(
          leftOperand,
          JOIN_TYPE, JOIN,
          rightOperand,
          ON,
          condition
        ).mkString(SPACE)
      }
      //where condition exp
      case SqlKind.GREATER_THAN
           | SqlKind.AND
           | SqlKind.OR
           | SqlKind.EQUALS => {
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
