package com.examples.p1_how_queries_are_expressed_as_ast.t1_select

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - FROM에 AS구문이 포함되어 있으면 어떤 노드로 표현되는지 확인한다.

SELECT col1, col2, col3
FROM TBL_1 AS A

root = {SqlSelect@1598} "SELECT `col1`, `col2`, `col3`\nFROM `TBL_1` AS `A`"
 selectList = {SqlNodeList@1677}  size = 3
 from = {SqlBasicCall@1678} "`TBL_1` AS `A`"
  operator = {SqlAsOperator@1767} "AS"
  operandList = {RegularImmutableList@1768}  size = 2
 */
object t1_2_select_as {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1 AS A
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
