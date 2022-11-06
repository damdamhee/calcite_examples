package com.examples.p2_build_querystr_from_ast

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.util.SqlVisitor
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
본 튜토리얼에서는 p1_how_queries_are_expressed에서 살펴본 쿼리 정도까지만
AST -> Query String으로 다시 변환할 수 있는 기능을 구현해볼 것이다.
 */
object Client {
  def main(args:Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val t1_1_select =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1
        |""".stripMargin
    val t1_1_select_result = query(t1_1_select, config, new SqlToStrVisitor_t1_1())
    println(t1_1_select_result) //ok

    val t1_2_select_as =
      """
        |  SELECT col1, col2, col3
        |  FROM TBL_1 AS A
        |""".stripMargin
    val t1_2_select_as_result = query(t1_2_select_as, config, new SqlToStrVisitor_t1_2());
    println(t1_2_select_as_result)

    val t1_3_select_with_functions =
      """
        |SELECT col1, col2, CAST(col3 AS INT)
        |FROM TBL_1
        |""".stripMargin
    val t1_3_select_with_functions_result = query(t1_3_select_with_functions, config, new SqlToStrVisitor_t1_3());
    println(t1_3_select_with_functions_result)

    val t1_4_select_with_functions =
      """
        |SELECT col1, LOWER(col2), CAST(col3 AS INT)
        |FROM TBL_1
        |""".stripMargin
    val t1_4_select_with_functions_result = query(t1_4_select_with_functions, config, new SqlToStrVisitor_t1_4());
    println(t1_4_select_with_functions_result)

    val t2_1_select_with_functions =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1
        |WHERE col1 > 10
        |""".stripMargin
    val t2_1_select_with_functions_result = query(t2_1_select_with_functions, config, new SqlToStrVisitor_t2_1());
    println(t2_1_select_with_functions_result)

    val t2_2_select_with_functions =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1
        |WHERE col1 > 10 AND col2 > 100
        |""".stripMargin
    val t2_2_select_with_functions_result = query(t2_2_select_with_functions, config, new SqlToStrVisitor_t2_2());
    println(t2_2_select_with_functions_result)

    val t2_3_select_with_functions =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1
        |WHERE col1 > 10 AND col2 > 100 OR col3 IS NOT NULL
        |""".stripMargin
    val t2_3_select_with_functions_result = query(t2_3_select_with_functions, config, new SqlToStrVisitor_t2_3());
    println(t2_3_select_with_functions_result)

    val t3_1_select_with_functions =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1 A JOIN TBL_2 B ON A.col1 = B.col1
        |""".stripMargin
    val t3_1_select_with_functions_result = query(t3_1_select_with_functions, config, new SqlToStrVisitor_t3_1());
    println(t3_1_select_with_functions_result)

    val t3_2_select_with_functions =
      """
        |SELECT col1, col2, col3
        |FROM TBL_1 A LEFT OUTER JOIN TBL_2 B ON A.col1 = B.col1
        |""".stripMargin
    val t3_2_select_with_functions_result = query(t3_2_select_with_functions, config, new SqlToStrVisitor_t3_2());
    println(t3_2_select_with_functions_result)

    val t4_1_select_with_functions =
      """
        |SELECT col1, col2, col3
        |FROM (
        |  SELECT col1, col2, col3
        |)
        |""".stripMargin
    val t4_1_select_with_functions_result = query(t4_1_select_with_functions, config, new SqlToStrVisitor_t4_1());
    println(t4_1_select_with_functions_result)

    val t5_1_select_with_functions =
      """
        |WITH TBL_1 AS (
        |  SELECT col1, col2, col3
        |  FROM GAMELOG
        |), TBL_2 AS (
        | SELECT col1, col2, col3
        | FROM ACCOUNT
        |)
        |
        |SELECT col1, col2, col3
        |FROM TBL_1
        |""".stripMargin
    val t5_1_select_with_functions_result = query(t5_1_select_with_functions, config, new SqlToStrVisitor_t5_1());
    println(t5_1_select_with_functions_result)
  }

  def query(sql:String, config:Config, visitor:SqlVisitor[String]):String = {
    val sqlParser = SqlParser.create(sql, config);
    val root = sqlParser.parseQuery(sql);

    root.accept(visitor)
  }

}
