新建项目，会自动生成src目录
mvn archetype:generate -DgroupId=com.aliyun.domain.sample -DartifactId=domain-sdk-sample -Dpackage=com.aliyun.domain.sample -Dversion=1.0-SNAPSHOT
目录结构为
domain-sdk-sample/src/main/java/com/aliyun/domain/sample
编译
mvn clean package -Dmaven.test.skip=true install
执行这一个方法
mvn -q exec:java -Dexec.mainClass=com.aliyun.domain.sample.App
