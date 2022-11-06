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
class SqlToStrVisitor_t5_1 extends SqlVisitor[String] {
  /*
  아래 쿼리를 처리할 수 있는 Visitor를 구현한다.
   */
  val SPACE = " ";
  val COMMA = ", "
  val OPEN_BRACKET = "("
  val CLOSE_BRACKET = ")"

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
      case SqlKind.WITH => {
        val root = call.asInstanceOf[SqlWith]
        val withItems:String = root.withList.accept(this)
        val body:String = root.body.accept(this)
        List(
          "WITH",
          withItems,
          body
        ).mkString(SPACE)
      }
      case SqlKind.WITH_ITEM => {
        val root = call.asInstanceOf[SqlWithItem]
        val table = root.name.accept(this)
        val query = root.query.accept(this)

        List(
          table,
          "AS",
          OPEN_BRACKET,
          query,
          CLOSE_BRACKET
        ).mkString(SPACE)
      }
      case SqlKind.SELECT => {
        val queryStr = scala.collection.mutable.ListBuffer[String]()
        val root = call.asInstanceOf[SqlSelect]

        //select절 처리
        val selectList = root.getSelectList
        queryStr ++= List("SELECT", selectList.accept(this));

        //from절 처리
        queryStr ++= (Option(root.getFrom) match {
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
          JOIN_TYPE,
          JOIN,
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
