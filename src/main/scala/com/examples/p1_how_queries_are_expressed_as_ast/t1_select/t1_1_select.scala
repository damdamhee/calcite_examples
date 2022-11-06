package com.examples.p1_how_queries_are_expressed_as_ast.t1_select

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - SELECT 쿼리의 루트가 무엇인지 확인한다.

SELECT col1, col2, col3
FROM TBL_1

root = {SqlSelect@1606} "SELECT `col1`, `col2`, `col3`\nFROM `TBL_1`"
 selectList = {SqlNodeList@1676}  size = 3
  0 = {SqlIdentifier@1772} "col1"
  1 = {SqlIdentifier@1773} "col2"
  2 = {SqlIdentifier@1774} "col3"
 from = {SqlIdentifier@1677} "TBL_1"

 */
object t1_1_select {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
