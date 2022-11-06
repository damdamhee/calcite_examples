package com.examples.p1_how_queries_are_expressed_as_ast.t1_select

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - SELECT에 수식이 표현되어 있으면 어떻게 되는지 확인한다.

SELECT col1, LOWER(col2), CAST(col3 AS INT)
FROM TBL_1

root = {SqlSelect@1602} "SELECT `col1`, LOWER(`col2`), CAST(`col3` AS INTEGER)\nFROM `TBL_1`"
 selectList = {SqlNodeList@1684}  size = 3
  0 = {SqlIdentifier@1776} "col1"
  1 = {SqlBasicCall@1777} "LOWER(`col2`)"
  2 = {SqlBasicCall@1778} "CAST(`col3` AS INTEGER)"
 from = {SqlIdentifier@1685} "TBL_1"
 */
object t1_4_select_with_functions {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT col1, LOWER(col2), CAST(col3 AS INT)
        |FROM TBL_1
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
