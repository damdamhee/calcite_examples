package com.examples.p1_how_queries_are_expressed_as_ast.t2_where

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - 2개의 조건식을 포함하는 WHERE가 어떻게 표현되는지 확인한다.

SELECT col1, col2, col3
FROM TBL_1
WHERE col1 > 10 AND col2 > 100

root = {SqlSelect@1674} "SELECT `col1`, `col2`, `col3`\nFROM `TBL_1`\nWHERE `col1` > 10 AND `col2` > 100"
 where = {SqlBasicCall@1696} "`col1` > 10 AND `col2` > 100"
  operator = {SqlBinaryOperator@1787} "AND"
  operandList = {RegularImmutableList@1788}  size = 2
   0 = {SqlBasicCall@1793} "`col1` > 10"
    operator = {SqlBinaryOperator@1801} ">"
    operandList = {RegularImmutableList@1802}  size = 2
      0 = {SqlIdentifier@1815} "col1"
      1 = {SqlNumericLiteral@1816} "10"
   1 = {SqlBasicCall@1794} "`col2` > 100"
    operator = {SqlBinaryOperator@1801} ">"
    operandList = {RegularImmutableList@1806}  size = 2
      0 = {SqlIdentifier@1810} "col2"
      1 = {SqlNumericLiteral@1811} "100"
 selectList = {SqlNodeList@1694}  size = 3
   0 = {SqlIdentifier@1820} "col1"
   1 = {SqlIdentifier@1821} "col2"
   2 = {SqlIdentifier@1822} "col3"
 from = {SqlIdentifier@1695} "TBL_1"
*/
object t2_2_select_from_where {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1
        |WHERE col1 > 10 AND col2 > 100
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
