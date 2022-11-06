//package com.examples
//
//import org.apache.calcite.config.Lex
//import org.apache.calcite.sql.parser.SqlParser
//import org.apache.calcite.sql.parser.SqlParser.Config
//import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
//import org.apache.calcite.sql.util.{SqlBasicVisitor, SqlVisitor}
//import org.apache.calcite.sql.validate.SqlConformanceEnum
//import org.apache.calcite.sql.{SqlBasicCall, SqlCall, SqlDataTypeSpec, SqlDynamicParam, SqlIdentifier, SqlIntervalQualifier, SqlLiteral, SqlNode, SqlNodeList, SqlSelect}
//import org.apache.spark.sql.functions.col
//import org.apache.spark.sql.{Column, DataFrame, SparkSession}
//
//import scala.collection.JavaConverters._
//
///*
//Q. SqlVisitor를 구현한 클래스의 visit()메소드에서는 어떤 알고리즘을 구현해야 할까?
//  Q.
//*/
//object t3_dataframe_from_sqlnode {
//  val spark = SparkSession.builder()
//    .appName("TestSpark")
//    .master("local")
//    .config("spark.driver.host", "127.0.0.1")
//    .config("spark.sql.shuffle.partitions", 5)
//    .getOrCreate();
//
//  import spark.implicits._
//
//
//
//  def main(args:Array[String]) = {
//    val df1 = Seq(
//      ("jh9310s1", 10, 100),
//      ("jh9310s2", 20, 200),
//      ("jh9310s3", 30, 300),
//      ("jh9310s4", 40, 400)
//    ).toDF("col1", "col2", "col3")
////    val df2 = Seq(
////      ("", 10, 100),
////      ("jh9310s20", 20, 200),
////      ("jh9310s", 30, 300),
////      ("jh9310s4", 40, 400)
////    ).toDF("col1", "col2", "col3")
//    val config:Config = Config.DEFAULT
//      .withLex(Lex.JAVA)
//      .withConformance(SqlConformanceEnum.BABEL)
//      .withParserFactory(SqlBabelParserImpl.FACTORY);
//
//    val sql:String =
//      """
//        |SELECT col1, col2
//        |FROM (
//        | SELECT * FROM MyDB.MyTable1
//        | WHERE col2 > 20
//        |)
//      """.stripMargin;
//    val sqlParser:SqlParser = SqlParser.create(sql, config);
//    val cols:List[Column] = List(col("col1"), col("col2"));
//
//    val datasources:scala.collection.mutable.Map[String, DataFrame] = scala.collection.mutable.Map(
//      "MyDB.MyTable1" -> df1,
//    )
//
//    val sqlConverter = new SqlToDataFrameConverter(
//      sqlParser,
//      new DataFrameBuilder(datasources, cols)
//    );
//
//    val retDf = sqlConverter.convert(sql);
//    retDf.show();
//  }
//}
//
//class SqlToDataFrameConverter(sqlParser:SqlParser, dfb:DataFrameBuilder) {
//  def convert(sql:String): DataFrame = {
//    val sqlNode:SqlNode = sqlParser.parseQuery(sql)
//    sqlNode.accept(dfb)
//  }
//}
//
//class DataFrameBuilder(val datasources:scala.collection.mutable.Map[String, DataFrame], val cols:List[Column]) extends SqlVisitor[DataFrame]{
//
//  override def visit(literal: SqlLiteral): DataFrame = ???
//
//  //1. 쿼리를 요청했을 때, 제일 먼저 이 메소드를 통해 접근될 것이다.
//  override def visit(call: SqlCall): DataFrame = {
//    call match {
//      case c:SqlSelect => { //실제로는 Select 직전까지의 DataFrame만 반환한다?
//        /*
//          1. FROM 처리
//          2. WHERE 처리
//          3. Group 처리
//          4. Having 처리
//          5. OrderBy는 어떻게 처리??
//        */
//        /*
//        from > where > groupBy > having > select > orderBy
//         */
//        //* 추출 대상 컬럼 목록을 SqlNode로부터 뽑으려고 하니... visit(SqlIdentifier)에서 FromClause와 걸림
////        val cols2 = selectCols.getList.asScala.map(c => c.)
////        val cols2 = c.getSelectList.iterator().asScala
////          .map(c => col(c.asInstanceOf[SqlIdentifier].names.get(0))).toList
//        SelectClause(
//          WhereClause(c.getWhere, this)(FromClause(c.getFrom, datasources)()),
//          this
//        ).apply(cols)
//      }
//      case w: SqlBasicCall => {
//        println("sqlBasicCall", w)
//        //연산자를 파악해서 where 구문을 적절히 만들어야 하는데...
//      }
//    }
//  }
//
//  //* 컬럼 목록일 때 이곳으로 온다. 하지만 DataFrame을 반환한다.
//  //  그렇다면, 무조건 컬럼 목록일 때만 이곳으로 오는 것일까?
//  override def visit(nodeList: SqlNodeList): DataFrame = ???
//
//  /*
//  ex. SELECT * FROM MyDB.MyTable
//      visit(SysCall) -> visit(SqlIdentifier)
//  */
//  override def visit(id: SqlIdentifier): DataFrame = {
//    //* SqlIdentifier에 해당되는 것들로 무엇이 있을까?
//    //  만약 테이블 명 이외의 것이 오면 문제가 생길 것이다.
//    //  길이로 구분해야 하나? ex. split(DB.Table, ',').len = 2
//    //  SqlVisitor에서는 SqlCall(ex. SqlSelect, SqlBasicCall(where),) 관련 로직은 하지 않는 것이 좋아 보임.
//    ???
//  }
//
//  override def visit(`type`: SqlDataTypeSpec): DataFrame = ???
//
//  override def visit(param: SqlDynamicParam): DataFrame = ???
//
//  override def visit(intervalQualifier: SqlIntervalQualifier): DataFrame = ???
//}
