新建项目，会自动生成src目录
mvn archetype:generate -DgroupId=com.aliyun.ecs.sample -DartifactId=ecs-sdk-sample -Dpackage=com.aliyun.ecs.sample -Dversion=1.0-SNAPSHOT
目录结构为
ecs-sdk-sample/src/main/java/com/aliyun/ecs/sample
编译
mvn clean package -Dmaven.test.skip=true install
执行这一个方法
mvn -q exec:java -Dexec.mainClass=com.aliyun.ecs.sample.App
