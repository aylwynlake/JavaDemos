本地安装scala maven archetype template
mvn install

使用maven archetype:
1) 命令行方式
创建demo项目
mvn archetype:generate -B ^
  -DarchetypeGroupId=ly.archetype ^
  -DarchetypeArtifactId=scala-template ^
  -DarchetypeVersion=1.0 ^
  -DgroupId=ly.test ^
  -DartifactId=scalatest ^
  -Dversion=0.1-SNAPSHOT

eclipse导入maven项目

2) eclipse导入本地archetype
new maven project -> add archetype...
加完后本地多了一个文件
${user.dir}/.m2/archetype-catalog.xml