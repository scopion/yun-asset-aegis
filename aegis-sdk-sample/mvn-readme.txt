新建项目，会自动生成src目录
mvn archetype:generate -DgroupId=com.aliyun.aegis.sample -DartifactId=aegis-sdk-sample -Dpackage=com.aliyun.aegis.sample -Dversion=1.0-SNAPSHOT
目录结构为
aegis-sdk-sample/src/main/java/com/aliyun/aegis/sample
编译
mvn clean package -Dmaven.test.skip=true install
执行这一个方法
mvn -q exec:java -Dexec.mainClass=com.aliyun.aegis.sample.App
