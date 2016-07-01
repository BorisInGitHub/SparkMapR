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
 * <p>
 * <p>
 * Idées pour débloquer :
 * - Utiliser SparkSubmit : https://github.com/cloudera/oozie/blob/cdh5.5.0-release/sharelib/spark/src/main/java/org.apache.oozie.action.hadoop/SparkMain.java
 * <p>
 * Created by breynard on 11/12/15.
 */
public class Test {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        LOGGER.info("Démarrage");
        long start = System.currentTimeMillis();


        // Configuration du working directory à

        // Configuration du hadoop.home.dir (sur le cluster ou sur mon poste ????)
        //System.setProperty("hadoop.home.dir", "/opt/cloudera/parcels/CDH/lib/hadoop");
        //System.setProperty("hadoop.home.dir", "/home/breynard/IdeaProjects/SparkYarn/src/main/resources");

        // Comment configurer le SPARK_LOCAL_IP ???
        //System.setProperty("SPARK_LOCAL_IP", "192.168.1.250");

        // Configuration du user SPARK
        System.setProperty("HADOOP_USER_NAME", "spark"); // Or Add VM option : -DHADOOP_USER_NAME=spark
//        System.setProperty("HADOOP_CONF_DIR", "/home/breynard/IdeaProjects/SparkYarn/src/conf/cloudera/yarn-conf");
//        System.setProperty("YARN_CONF_DIR", "/home/breynard/IdeaProjects/SparkYarn/src/conf/cloudera/yarn-conf");

        //set HADOOP_CONF_DIR=/home/breynard/IdeaProjects/SparkYarn/cloudera/src/conf


        //System.setProperty("HADOOP_CONF_DIR", "/home/breynard/IdeaProjects/SparkYarn/cloudera/src/conf/");
        //System.setProperty("YARN_CONF_DIR", "/home/breynard/IdeaProjects/SparkYarn/cloudera/src/conf/");

        //System.setProperty("hadoop.home.dir", "/home/breynard/IdeaProjects/SparkYarn/cloudera/src/conf");


        // TODO : Autre Option VM : -DMR2_CLASSPATH=/opt/cloudera/parcels/CDH/jars/
        // TODO : Autre Option VM : -DHADOOP_CLASSPATH=/opt/cloudera/parcels/CDH/jars/

        //System.setProperty("YARN_CONF_DIR", "/home/breynard/aggrego/yarn-conf/");
        //System.setProperty("MR2_CLASSPATH", "/home/breynard/IdeaProjects/SparkYarn/target/");

        boolean resultOk = false;

        long startAfterInit = 0;
        // Copie des fichiers dans le classpath ...
        try (JavaSparkContext javaSparkContext = modeCDHSimple()) {

            startAfterInit = System.currentTimeMillis();
            LOGGER.info("UI on http://localhost:4040");
            List<String> data = Arrays.asList("a", "b", "c", "d");

            //val file = sc.textFile("hdfs://vagrant-ubuntu-trusty-64:8020/user/spark/aggrego/test.csv")

            //javaSparkContext.parallelize(data).saveAsTextFile("hdfs://vagrant-ubuntu-trusty-64:8020/user/spark/aggrego/test7.csv");
            //LOGGER.info("----------------\n\n\n\n\n\n\n\n\n\n\n\n\nOK BORIS.");

            long collect = javaSparkContext.parallelize(data).count();
            LOGGER.info("----------------\n\n\n\n\n\n\n\n\n\n\n\n\nResult {}.", collect);
            resultOk = collect == data.size();

        } catch (Throwable t) {
            LOGGER.error("Error ", t);
        }
        LOGGER.info("Result {} in Duration {}ms (total {}ms).", resultOk, System.currentTimeMillis() - startAfterInit, System.currentTimeMillis() - start);
        System.exit(0);
    }


    private static JavaSparkContext modeCDH() {
        SparkConf sparkConf = new SparkConf();

        //List<String> jars = new ArrayList<>(Arrays.asList(JavaSparkContext.jarOfClass(Test.class)));
        //List<String> jars = new ArrayList<>();
        //jars.add("hdfs:///user/spark/spark-assembly.jar");
        //jars.add("hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-worker.jar");

        sparkConf.setAppName("Aggrego Test")
                //.setMaster("yarn-cluster")
                .setMaster("yarn-client")

                //.setJars(jars.toArray(new String[jars.size()]))

                //.setExecutorEnv("SPARK_LOCAL_IP", "192.168.1.16")

                //# sudo -u hdfs hdfs dfs -mkdir -p /user/spark
                //# sudo -u hdfs hdfs dfs -put /usr/lib/spark/lib/spark-assembly.jar /user/spark/spark-assembly.jar

                // Copie du fichier dans HDFS via
                // sudo -u spark hdfs dfs -put /home/vagrant/sparkYarn-1.0-SNAPSHOT-all.jar /user/spark/sparkYarn-1.0-SNAPSHOT-all.jar
                //.set("spark.yarn.jar", "hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-worker.jar,hdfs:///user/spark/spark-assembly.jar")


                // JB:
                .set("spark.files.overwrite", "true")

                .set("spark.yarn.jar", "hdfs:///user/cloudera/spark-assembly.jar")
                .set("spark.yarn.am.extraLibraryPath", "hdfs:///user/cloudera/sparkYarn-1.0-SNAPSHOT-worker.jar")
                //.set("spark.yarn.dist.files", "hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-worker.jar")


//        private static final String EXECUTOR_CLASSPATH = "spark.executor.extraClassPath=";
//        private static final String DRIVER_CLASSPATH = "spark.driver.extraClassPath=";

                //.set("spark.yarn.jar", "hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-worker2.jar")
                //.set("spark.yarn.jar", "hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-*.jar")//hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-driver.jar")

                //.set("spark.yarn.am.cores","2")

                // Mode dynamic Allocation
                .set("spark.dynamicAllocation.enabled", "false")
                .set("spark.executor.cores", "2")
                .set("spark.driver.cores", "2")
                .set("spark.hadoop.cloneConf", "true")

                .set("spark.deploy.defaultCores", "2")
                .set("spark.cores.max", "2")


                .set("spark.dynamicAllocation.initialExecutors", "2")
                .set("spark.dynamicAllocation.minExecutors", "2")

                .set("spark.executor.instances", "2")
                .set("spark.cores.max", "2")


                //.set("spark.shuffle.service.enabled","true")

                .set("spark.driver.log.level", "INFO")
                .set("spark.eventLog.dir", "hdfs:///user/spark/")
                .set("spark.eventLog.enabled", "true")

        //.set("spark.yarn.historyServer.address", "http://vagrant-ubuntu-trusty-64:18080")
  /*              .set("YARN_CONF_DIR", "/home/breynard/aggrego/yarn-conf/")
                .set("spark.local.dir", "/home/breynard/aggrego/")
                .setExecutorEnv("YARN_CONF_DIR", "/home/breynard/aggrego/yarn-conf/")*/
        ;


        SparkContext sparkContext = new SparkContext(sparkConf);


        // sparkContext.hadoopConfiguration().reloadConfiguration();

        //sparkContext.schedulerBackend().start();
        //sparkContext.taskScheduler().start();
        return new JavaSparkContext(sparkContext);
    }

    private static JavaSparkContext modeCDHSimple() {
        // Copie dans les ressources de yarn-conf (conf/cloudera/yarn-conf)

        // Changement côté serveur :
        // yarn.nodemanager.resource.memory-mb à 2500

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("Aggrego Test")
                .setMaster("yarn-client")

                .set("spark.executor.memory", "512M")
//                .set("spark.executor.cores", "1") // 1 in YARN mode, all the available cores on the worker in standalone mode.

                //.set("spark.deploy.defaultCores", "1")
//                .set("spark.cores.max","2")
                .set("spark.yarn.maxAppAttempts", "10")

                //.set("spark.driver.host","quickstart.cloudera")
                //.set("spark.driver.port","38634")

                // Log SPARK
                .set("spark.driver.log.level", "INFO")
                .set("spark.eventLog.dir", "hdfs:///user/spark/")
                .set("spark.eventLog.enabled", "true")

                //# sudo -u spark hdfs dfs -mkdir -p /user/spark
                //# sudo -u spark hdfs dfs -put /opt/cloudera/parcels/CDH/lib/spark/lib/spark-assembly.jar /user/spark/spark-assembly.jar
                //# sudo -u spark hdfs dfs -put ~/sparkYarn-1.0-SNAPSHOT-worker.jar /user/spark/sparkYarn-1.0-SNAPSHOT-worker.jar
                //# sudo -u spark hdfs dfs -put ~/sparkYarn-1.0-SNAPSHOT-all.jar /user/spark/sparkYarn-1.0-SNAPSHOT-all.jar
                //# sudo -u spark hdfs dfs -chmod -R 777 /user/spark
                //# sudo -u spark hdfs dfs -ls /user/spark/

                //.set("spark.yarn.dist.files", "hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-all.jar")
                .set("spark.yarn.dist.files", "hdfs://cdh/user/spark/sparkYarnCDH-1.0-SNAPSHOT-worker.jar")
                .set("spark.yarn.jar", "hdfs://cdh/user/spark/spark-assembly.jar")
                .set("spark.yarn.am.extraLibraryPath", "hdfs://cdh/user/spark/sparkYarnCDH-1.0-SNAPSHOT-worker.jar")
        ;


        SparkContext sparkContext = new SparkContext(sparkConf);
        return new JavaSparkContext(sparkContext);
    }

    private static JavaSparkContext modeCDHSimple2() {
        // Copie dans les ressources de yarn-conf (conf/cloudera/yarn-conf)

        // Changement côté serveur :
        // yarn.nodemanager.resource.memory-mb à 2500

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("Aggrego Test")
                .setMaster("yarn-client")

                .set("spark.executor.memory", "512M")
                .set("spark.yarn.maxAppAttempts", "10")


                //.set("spark.driver.host","quickstart.cloudera")
                //.set("spark.driver.port","38634")

                // Log SPARK
//                .set("spark.driver.log.level", "INFO")
//                .set("spark.eventLog.dir", "hdfs:///user/cloudera/")
//                .set("spark.eventLog.enabled", "true")

                //# sudo -u spark hdfs dfs -mkdir -p /user/spark
                //# sudo -u spark hdfs dfs -put /opt/cloudera/parcels/CDH/lib/spark/lib/spark-assembly.jar /user/spark/spark-assembly.jar
                //# sudo -u spark hdfs dfs -put ~/sparkYarn-1.0-SNAPSHOT-worker.jar /user/spark/sparkYarn-1.0-SNAPSHOT-worker.jar
                //# sudo -u spark hdfs dfs -put ~/sparkYarn-1.0-SNAPSHOT-all.jar /user/spark/sparkYarn-1.0-SNAPSHOT-all.jar
                //# sudo -u spark hdfs dfs -chmod -R 777 /user/spark
                //# sudo -u spark hdfs dfs -ls /user/spark/


                //.set("spark.yarn.dist.files", "hdfs:///user/spark/sparkYarn-1.0-SNAPSHOT-all.jar")
                .set("spark.yarn.dist.files", "hdfs://cdhmasternode/user/cloudera/sparkYarnCDH-1.0-SNAPSHOT-worker.jar")
                .set("spark.yarn.jar", "hdfs://cdhmasternode/user/cloudera/spark-assembly.jar")
                .set("spark.yarn.am.extraLibraryPath", "hdfs://cdhmasternode/user/cloudera/sparkYarnCDH-1.0-SNAPSHOT-worker.jar")
        ;


        SparkContext sparkContext = new SparkContext(sparkConf);
        return new JavaSparkContext(sparkContext);
    }
}
