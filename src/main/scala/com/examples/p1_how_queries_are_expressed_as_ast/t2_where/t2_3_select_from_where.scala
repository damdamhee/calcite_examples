package com.examples.p1_how_queries_are_expressed_as_ast.t2_where

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - 3개의 조건식을 갖는 WHERE가 어떻게 표현되는지 확인한다.

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
object t2_3_select_from_where {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1
        |WHERE col1 > 10 AND col2 > 100 OR col3 IS NOT NULL
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
