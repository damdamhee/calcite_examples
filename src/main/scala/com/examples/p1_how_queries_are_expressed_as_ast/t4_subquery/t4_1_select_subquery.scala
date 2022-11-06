package com.examples.p1_how_queries_are_expressed_as_ast.t4_subquery

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - 서브쿼리는 어떻게 표현되는지 확인한다.

SELECT col1, col2, col3
FROM (
  SELECT col1, col2, col3
)

root = {SqlSelect@1606} "SELECT `col1`, `col2`, `col3`\nFROM (SELECT `col1`, `col2`, `col3`\nFROM `TBL_1`)"
 selectList = {SqlNodeList@1677}  size = 3
 from = {SqlSelect@1678} "SELECT `col1`, `col2`, `col3`\nFROM `TBL_1`"
  selectList = {SqlNodeList@1768}  size = 3
   0 = {SqlIdentifier@1777} "col1"
   1 = {SqlIdentifier@1778} "col2"
   2 = {SqlIdentifier@1779} "col3"
  from = {SqlIdentifier@1769} "TBL_1"
 */
object t4_1_select_subquery {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT col1, col2, col3
        |FROM (
        | SELECT col1, col2, col3
        | FROM TBL_1
        |)
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
