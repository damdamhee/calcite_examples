package com.examples.p1_how_queries_are_expressed_as_ast.t2_where

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - 하나의 조건식을 갖는 WHERE가 어떻게 표현되는지 확인한다.

SELECT col1, col2, col3
FROM TBL_1
WHERE col1 > 10

where = {SqlBasicCall@1696} "`col1` > 10"
 operator = {SqlBinaryOperator@1787} ">"
 operandList = {RegularImmutableList@1788}  size = 2
selectList = {SqlNodeList@1694}  size = 3
from = {SqlIdentifier@1695} "TBL_1"
*/
object t2_1_select_from_where {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1
        |WHERE col1 > 10
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
