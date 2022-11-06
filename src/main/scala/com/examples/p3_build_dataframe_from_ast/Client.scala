package com.examples.p3_build_dataframe_from_ast

import com.examples.p3_build_dataframe_from_ast.t1_2.{Context_t1_2, SqlToDataFrameVisitor_t1_2}
import org.apache.calcite.config.Lex
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.util.SqlVisitor
import org.apache.calcite.sql.validate.SqlConformanceEnum
import org.apache.spark.sql.{DataFrame, SparkSession}

/*
본 튜토리얼에서는 p1_how_queries_are_expressed에서 살펴본 쿼리 정도까지만
AST -> Spark Dataframe으로 변환할 수 있는 기능을 구현해볼 것이다.
 */
object Client {
    val spark = SparkSession.builder()
      .appName("TestSpark")
      .master("local")
      .config("spark.driver.host", "127.0.0.1")
      .config("spark.sql.shuffle.partitions", 5)
      .getOrCreate();

    import spark.implicits._

  def main(args:Array[String]): Unit = {
    val config: Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

//    val context = new Context(Map("TBL_1" -> {
//      Seq(
//        (10, 20, 30),
//        (100, 200, 300)
//      ).toDF("col1", "col2", "col3")
//    }))
//    val t1_1_select =
//      """
//        |SELECT col1, col2, col3
//        |FROM TBL_1
//        |""".stripMargin
//    val t1_1_select_result = query(t1_1_select, config, new SqlToDataFrameVisitor_t1_1(context))
//    t1_1_select_result.show()

    val context = new Context_t1_2(Map("TBL_1" -> {
      Seq(
        (10, 20, 30),
        (100, 200, 300)
      ).toDF("col1", "col2", "col3")
    }), Map())
    val t1_2 =
      """
        |SELECT col1, col2, A.col3
        |FROM TBL_1 AS A
        |""".stripMargin
    val t1_2_result = query(t1_2, config, new SqlToDataFrameVisitor_t1_2(context))
    t1_2_result.show()

  }

  def query(sql:String, config:Config, visitor:SqlVisitor[DataFrame]):DataFrame = {
    val sqlParser = SqlParser.create(sql, config);
    val root = sqlParser.parseQuery(sql);

    root.accept(visitor)
  }

}
