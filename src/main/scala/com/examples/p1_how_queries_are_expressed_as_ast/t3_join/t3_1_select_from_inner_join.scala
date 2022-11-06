package com.examples.p1_how_queries_are_expressed_as_ast.t3_join

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
목표 - 조인이 어떻게 표현되는지 확인한다.

SELECT col1, col2, col3
FROM TBL_1 A JOIN TBL_2 B ON A.col1 = B.col1

root = {SqlSelect@1668} "SELECT `A`.`col1`, `B`.`col2`, `B`.`col3`\nFROM `TBL_1` AS `A`\nINNER JOIN `TBL_2` AS `B` ON `A`.`col1` = `B`.`col1`"
 selectList = {SqlNodeList@1699}  size = 3
  0 = {SqlIdentifier@1790} "A.col1"
  1 = {SqlIdentifier@1791} "B.col2"
  2 = {SqlIdentifier@1792} "B.col3"
 from = {SqlJoin@1700} "SELECT *\nFROM `TBL_1` AS `A`\nINNER JOIN `TBL_2` AS `B` ON `A`.`col1` = `B`.`col1`"
  left = {SqlBasicCall@1797} "`TBL_1` AS `A`"
   operator = {SqlAsOperator@1812} "AS"
   operandList = {RegularImmutableList@1813}  size = 2
  natural = {SqlLiteral@1798} "FALSE"
  joinType = {SqlLiteral@1799} "INNER"
  right = {SqlBasicCall@1800} "`TBL_2` AS `B`"
   operator = {SqlAsOperator@1812} "AS"
   operandList = {RegularImmutableList@1821}  size = 2
 conditionType = {SqlLiteral@1792} "ON"
 condition = {SqlBasicCall@1793} "`A`.`col1` = `B`.`col1`"
  operator = {SqlBinaryOperator@1803} "="
  operandList = {RegularImmutableList@1804}  size = 2
  functionQuantifier = null
  pos = {SqlParserPos@1805} "line 3, column 30"
*/
object t3_1_select_from_inner_join {
  def main(args: Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql =
      """
        |SELECT A.col1, B.col2, B.col3
        |FROM TBL_1 A JOIN TBL_2 B ON A.col1 = B.col1
        |""".stripMargin

    val sqlParser = SqlParser.create(sql, config)
    val root = sqlParser.parseQuery(sql)
    println(root)
  }
}
