package com.examples.p2_build_querystr_from_ast

import org.apache.calcite.sql.util.SqlVisitor
import org.apache.calcite.sql._

import scala.collection.JavaConverters.asScalaBufferConverter

/*
SqlVisitor의 목적?
SqlVisitor 인터페이스는 AST 노드를 방문할 때마다 실행되어야 하는 로직을 정의한 Visitor 클래스이다.
SqlToStrVisitor 클래스는 AST 노드를 방문할 때마다 노드 -> QueryString으로 다시 변환하는 작업을 수행한다.
*/
class SqlToStrVisitor_t1_2 extends SqlVisitor[String] {
  /*
  아래 쿼리를 처리할 수 있는 Visitor를 구현한다.
  SELECT col1, col2, col3
  FROM TBL_1 AS A

  root = {SqlSelect@1598} "SELECT `col1`, `col2`, `col3`\nFROM `TBL_1` AS `A`"
   selectList = {SqlNodeList@1677}  size = 3
   from = {SqlBasicCall@1678} "`TBL_1` AS `A`"
    operator = {SqlAsOperator@1767} "AS"
    operandList = {RegularImmutableList@1768}  size = 2
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
      case SqlKind.AS => {
        val root = call.asInstanceOf[SqlBasicCall]

        val tableIdentifierNode = root.getOperandList.asScala(0)
        val tableAliasIdentifierNode = root.getOperandList.asScala(0)
        List(
          tableIdentifierNode.accept(this),
          "AS",
          tableAliasIdentifierNode.accept(this)
        ).mkString(SPACE)
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

  //이런식으로 이곳에 도달할 수는 없다.
  //이렇게 하려면 모든 SqlNode를 상속하는 새로운 Acceptor클래스를 구현하여야 한다.
  //  def visit(expression: SqlBasicCall): String = {
  //
  //    expression.getOperator.getKind match {
  //      case SqlKind.AS => {
  //        val tableIdentifierNode = expression.getOperandList.asScala(0)
  //        val tableAliasIdentifierNode = expression.getOperandList.asScala(0)
  //        List(
  //          tableIdentifierNode.accept(this),
  //          "AS",
  //          tableAliasIdentifierNode.accept(this)
  //        );
  //      }
  //    }
  //    "BASIC CALL"
  //  }
}
