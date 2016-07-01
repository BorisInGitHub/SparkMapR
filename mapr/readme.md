# Spark in mode yarn-client with MapR



###MapR sandbox
Download, Install and run the MapR sandbox (with Oracle VirtualBox in my case) MapR 5.1 : http://doc.mapr.com/display/MapR/MapR+Sandbox+for+Hadoop

Configure your /etc/hosts to known the ``maprdemo`` host (127.0.0.1)
* The server name is ``maprdemo``
* The web manager is on [http://maprdemo:8443]() mapr/mapr
* My MapR current version is v. 5.1.0.37549.GA

### Mapr From scratch
* Install Mapr on Ubuntu with root user
* Add Spark with : http://doc.mapr.com/display/MapR/Install+Spark+on+YARN


###Usage
I will try the yarn client mode to connect Spark to the MapR cluster (i don't want to use the spark-submit script).

The configuration of the Hadoop cluster can be found on [http://maprdemo:8088/conf]()

* Configure the **yarn-site.xml** in the resources directory (to connect correctly to Yarn with Spark).
We need the following properties :
  * yarn.resourcemanager.scheduler.address
  * yarn.resourcemanager.principal
  * yarn.resourcemanager.resource-tracker.address
  * yarn.resourcemanager.address
  * yarn.resourcemanager.hostname

* Configure the **core-site.xml** in the resources directory (to connect correctly to HDFS with Spark). We nedd the folowwing properties :
  * fs.defaultFS

* In the maven pom (pom.xml) add the dependency to mapr-fs (see [http://doc.mapr.com/display/MapR/Maven+Artifacts+for+MapR]())
```
        <repository>
            <id>mapr-releases</id>
            <url>http://repository.mapr.com/maven/</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <releases>
                <enabled>true</enabled>
            </releases>
        </repository>

...

    <properties>
        <spark.version>1.5.2</spark.version>
        <mapr.version>5.1.0-mapr</mapr.version>
    </properties>

...

        <dependency>
            <groupId>com.mapr.hadoop</groupId>
            <artifactId>maprfs</artifactId>
            <version>${mapr.version}</version>
        </dependency>
```

* Compilation with ```mvn clean install```
* Copy the worker on the hadoop cluster and put it on HDFS
```
# From the current directory
#cp target/sparkYarnMapR-1.0-SNAPSHOT-worker.jar /tmp/
scp -P 2222 target/sparkYarnMapR-1.0-SNAPSHOT-worker.jar mapr@maprdemo:///tmp/
#scp -P 2222 mapr@maprdemo:///opt/mapr/spark/spark-1.5.2/lib/spark-assembly-1.5.2-mapr-1602-hadoop2.7.0-mapr-1602.jar /tmp/

ssh mapr@maprdemo -p 2222
# On the host maprdemo do
cp /opt/mapr/spark/spark-1.5.2/lib/spark-assembly-1.5.2-mapr-1605-hadoop2.7.0-mapr-1605.jar /tmp/

hadoop fs -fs maprfs://my.cluster.com -mkdir -p /user/spark
hadoop fs -fs maprfs://my.cluster.com -put /opt/mapr/spark/spark-1.5.2/lib/spark-assembly-1.5.2-mapr-1605-hadoop2.7.0-mapr-1602.jar /user/spark/spark-assembly-1.5.2-mapr-1605-hadoop2.7.0-mapr-1602.jar
hadoop fs -fs maprfs://my.cluster.com -put /tmp/sparkYarnMapR-1.0-SNAPSHOT-worker.jar /user/spark/sparkYarnMapR-1.0-SNAPSHOT-worker.jar
hadoop fs -fs maprfs://my.cluster.com -chmod -R 777 /user/spark
hadoop fs -fs maprfs://my.cluster.com -ls /user/spark/
```

* Check the configuration in the Test.java, specially the following parameters for the sparkConfiguration
  * spark.yarn.dist.files
  * spark.yarn.jar
  * spark.yarn.am.extraLibraryPath

Notes:
 * But how to configure correctly the maprfs URI : somehting like "maprfs://hostname1:7222/mapr/my.cluster.com", see [http://maprdemo:7221/cldb.jsp]()
 * We can see in file /opt/mapr/conf/cldb.conf (on maprdemo host), than the current port is 7222
 * In /opt/mapr/conf/mapr-clusters.conf (on maprdemo host) we have the cluster name : demo.mapr.com
   * So I put my maprfs URI like this : maprfs://demo.mapr.com/tmp/XXX
   * Copy of file /opt/mapr/conf/mapr-clusters.conf from maprdemo to the current host
   ``scp -P 2222 mapr@maprdemo:///opt/mapr/conf/mapr-clusters.conf /opt/mapr/conf/mapr-clusters.conf``
 * We can see my files on maprdemo host : in directory /mapr/demo.mapr.com/user/spark/
 * MaprFS : Usage see [http://doc.mapr.com/display/MapR/Accessing+MapR-FS+in+Java+Applications]()

* Launch the Test Java application




