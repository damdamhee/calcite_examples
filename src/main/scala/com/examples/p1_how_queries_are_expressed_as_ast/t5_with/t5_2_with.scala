package com.examples.p1_how_queries_are_expressed_as_ast.t5_with

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - WITH절만 존재하여도 문제가 없는지 확인한다.

WITH TBL_1 AS (
  SELECT col1, col2, col3
  FROM GAMELOG
)

org.apache.calcite.sql.parser.SqlParseException
*/
object t5_2_with {
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
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
