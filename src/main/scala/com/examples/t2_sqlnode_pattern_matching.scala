package com.examples

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.{SqlIdentifier, SqlNode, SqlSelect}
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.util.SqlVisitor
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
Example#2 - SqlSelect로부터 컬럼 명들을 파싱해보자
*/
object t2_sqlnode_pattern_matching {
  def main(args:Array[String]) = {
    val config:Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    val sql:String = "SELECT col1, col2 FROM MyDB.MyTable1";
    val sqlParser:SqlParser = SqlParser.create(sql, config);
    val sqlNode:SqlNode = sqlParser.parseQuery(sql);

    val res = sqlNode match {
      case node:SqlSelect => "SqlSelect"
      case identifier:SqlIdentifier => "SqlIdentifier"
      case _ => "etc"
    }
    println(res); //sqlSelect
  }
}
