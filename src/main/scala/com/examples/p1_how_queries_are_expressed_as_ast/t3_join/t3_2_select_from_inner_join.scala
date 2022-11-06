package com.examples.p1_how_queries_are_expressed_as_ast.t3_join

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - 조인이 어떻게 표현되는지 확인한다.

SELECT col1, col2, col3
FROM TBL_1 A LEFT OUTER JOIN TBL_2 B ON A.col1 = B.col1

root = {SqlSelect@1657} "SELECT `A`.`col1`, `B`.`col2`, `B`.`col3`\nFROM `TBL_1` AS `A`\nLEFT JOIN `TBL_2` AS `B` ON `A`.`col1` = `B`.`col1`"
 selectList = {SqlNodeList@1697}  size = 3
  0 = {SqlIdentifier@1788} "A.col1"
  1 = {SqlIdentifier@1789} "B.col2"
  2 = {SqlIdentifier@1790} "B.col3"
 from = {SqlJoin@1698} "SELECT *\nFROM `TBL_1` AS `A`\nLEFT JOIN `TBL_2` AS `B` ON `A`.`col1` = `B`.`col1`"
  left = {SqlBasicCall@1795} "`TBL_1` AS `A`"
  natural = {SqlLiteral@1796} "FALSE"
  joinType = {SqlLiteral@1797} "LEFT"
  right = {SqlBasicCall@1798} "`TBL_2` AS `B`"
  conditionType = {SqlLiteral@1791} "ON"
  condition = {SqlBasicCall@1792} "`A`.`col1` = `B`.`col1`"
   operator = {SqlBinaryOperator@1802} "="
   operandList = {RegularImmutableList@1803}  size = 2
   functionQuantifier = null
   pos = {SqlParserPos@1804} "line 3, column 41"
*/
object t3_2_select_from_inner_join {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT A.col1, B.col2, B.col3
        |FROM TBL_1 A LEFT OUTER JOIN TBL_2 B ON A.col1 = B.col1
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
