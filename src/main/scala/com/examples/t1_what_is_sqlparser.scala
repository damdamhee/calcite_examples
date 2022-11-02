package com.examples

import org.apache.calcite.config.Lex
import org.apache.calcite.sql.SqlNode
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.parser.SqlParser.Config
import org.apache.calcite.sql.parser.babel.SqlBabelParserImpl
import org.apache.calcite.sql.validate.SqlConformanceEnum

/*
Example#1 - SqlNode 구조 살펴보기
SQL을 파싱하면 SqlNode를 반환받는다. SqlNode로는 다음과 같은 것들이 존재한다.
* SqlNode
  * SqlCall
    * SqlAlter
    * SqlAttributeDefinition
    * SqlBasicCall - Implementation of SqlCall that keeps its operands in an array.
    * SqlCase
    * SqlCheckConstraint
    * SqlColumnDeclaration
    * SqlDdl
    * SqlDelete
    * SqlDescribeSchema
    * SqlDescribeTable
    * SqlExplain
    * SqlHint
    * SqlInsert
    * SqlJoin
    * SqlKeyConstraint
    * SqlMatchRecognize
    * SqlMerge
    * SqlOrderBy
    * SqlPivot
    * SqlSelect
    * SqlSnapshot
    * SqlTableRef
    * SqlUnpivot
    * SqlUpdate
    * SqlWindow
    * SqlWith
    * SqlWithItem
  * SqlNodeList
  * SqlIdentifier

다음은 SQL을 파싱했을 때 반환받은 SqlNode에 포함된 내용이다.
sql = "SELECT col1, col2 FROM MyDB.MyTable1"
sqlNode {SqlSelect@1273}
  where = null
  keywordList = {SqlNodeList@1351}  size = 0
  selectList = {SqlNodeList@1352}  size = 2
    0 = {SqlIdentifier@1508} "col1"
    1 = {SqlIdentifier@1509} "col2"
  from = {SqlIdentifier@1353} "MyDB.MyTable1"
  groupBy = null
  having = null
  windowDecls = {SqlNodeList@1354}  size = 0
  orderBy = null
  offset = null
  fetch = null
  hints = {SqlNodeList@1355}  size = 0
  pos = {SqlParserPos@1356} "line 1, column 1"
*/
object t1_what_is_sqlparser {
  def main(args:Array[String]) = {
    val config:Config = Config.DEFAULT
      .withLex(Lex.JAVA)
      .withConformance(SqlConformanceEnum.BABEL)
      .withParserFactory(SqlBabelParserImpl.FACTORY);

    /*
    create() : 설정(config)에 따라 전달한 sql을 파싱할 수 있는 SqlParser를 반환한다.
     */
    val sql:String = "SELECT col1, col2 FROM MyDB.MyTable1";
    val sqlParser:SqlParser = SqlParser.create(sql, config);
    val sqlNode:SqlNode = sqlParser.parseQuery(sql);
    System.out.println(sqlNode);
  }
}
