package com.semsoft.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Test pour SPARK sur Yarn
 */
public class Test {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);


    public static void main(String[] args) {
        LOGGER.info("Démarrage");
        try (JavaSparkContext javaSparkContext = modeMapR()) {
            List<String> data = Arrays.asList("a", "b", "c", "d");
            long collect = javaSparkContext.parallelize(data).count();
            LOGGER.info("----------------\n\n\n\n\n\n\n\n\n\n\n\n\nResult {}.", collect);

        } catch (Throwable t) {
            LOGGER.error("Error ", t);
        }
        System.exit(0);
    }

    private static JavaSparkContext modeMapR() {

        System.setProperty("HADOOP_USER_NAME", "root");

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("Aggrego Test")
                .setMaster("yarn-client")

                .set("spark.executor.memory", "512M")
                .set("spark.yarn.maxAppAttempts", "10")

//                .set("spark.yarn.jar", "/tmp/spark-assembly-1.5.2-mapr-1602-hadoop2.7.0-mapr-1602.jar")
//                .set("spark.yarn.dist.files", "/tmp/sparkYarn-1.0-SNAPSHOT-worker.jar")
//                .set("spark.yarn.am.extraLibraryPath", "/tmp/sparkYarn-1.0-SNAPSHOT-worker.jar")

//                .set("spark.yarn.dist.files", "hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-worker.jar")
//                .set("spark.yarn.jar", "hdfs:///user/spark/spark-assembly-1.5.2-mapr-1602-hadoop2.7.0-mapr-1602.jar")
//                .set("spark.yarn.am.extraLibraryPath", "hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-worker.jar")

                // something like "maprfs://hostname1:7222/mapr/my.cluster.com see http://maprdemo:8443/mcs#cldb


                // maprfs://demo.mapr.com/tmp/sparkYarn-1.0-SNAPSHOT-worker.jar or maprfs://maprdemi:7222/demo.mapr.com/tmp/sparkYarn-1.0-SNAPSHOT-worker.jar
                .set("spark.yarn.dist.files", "maprfs://my.cluster.com/user/spark/sparkYarnMapR-1.0-SNAPSHOT-worker.jar")
                .set("spark.yarn.jar", "maprfs://my.cluster.com/user/spark/spark-assembly-1.5.2-mapr-1605-hadoop2.7.0-mapr-1602.jar")
                .set("spark.yarn.am.extraLibraryPath", "maprfs://my.cluster.com/user/spark/sparkYarnMapR-1.0-SNAPSHOT-worker.jar");

                // Log Spark
//                .set("spark.driver.log.level", "INFO")
//                .set("spark.eventLog.dir", "hdfs:///user/spark/")
//                .set("spark.eventLog.enabled", "true");


        SparkContext sparkContext = new SparkContext(sparkConf);
        // TODO essayer avec un addJar et enlever le spark.yarn.dist.files et le spark.yarn.am.extraLibraryPath
        //sparkContext.addJar();
        return new JavaSparkContext(sparkContext);
    }
}
