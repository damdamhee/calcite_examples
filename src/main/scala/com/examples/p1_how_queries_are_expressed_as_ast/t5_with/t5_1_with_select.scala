package com.examples.p1_how_queries_are_expressed_as_ast.t5_with

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - WITH절이 포함된 쿼리는 트리가 어떻게 구성되는지 확인한다.

WITH TBL_1 AS (
  SELECT col1, col2, col3
  FROM GAMELOG
)
SELECT col1, col2, col3
FROM TBL_1

root = {SqlWith@1597} "WITH `TBL_1` AS (SELECT `col1`, `col2`, `col3`\nFROM `GAMELOG`) (SELECT `col1`, `col2`, `col3`\nFROM `TBL_1`)"
 withList = {SqlNodeList@1678}  size = 1
  0 = {SqlWithItem@1776} "`TBL_1` AS SELECT `col1`, `col2`, `col3`\nFROM `GAMELOG`"
   name = {SqlIdentifier@1778} "TBL_1"
   columnList = null
   query = {SqlSelect@1779} "SELECT `col1`, `col2`, `col3`\nFROM `GAMELOG`"
 body = {SqlSelect@1679} "SELECT `col1`, `col2`, `col3`\nFROM `TBL_1`"
  selectList = {SqlNodeList@1767}  size = 3
   0 = {SqlIdentifier@1789} "col1"
   1 = {SqlIdentifier@1790} "col2"
   2 = {SqlIdentifier@1791} "col3"
  from = {SqlIdentifier@1768} "TBL_1"
*/
object t5_1_with_select {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |WITH TBL_1 AS (
        |  SELECT col1, col2, col3
        |  FROM GAMELOG
        |)
        |SELECT col1, col2, col3
        |FROM TBL_1
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
