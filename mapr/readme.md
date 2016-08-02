# Spark in mode yarn-client with MapR



###MapR sandbox
Download, Install and run the MapR sandbox (with Oracle VirtualBox in my case) MapR 5.1 : [http://doc.mapr.com/display/MapR/MapR+Sandbox+for+Hadoop]()

Configure your /etc/hosts to known the ``maprdemo`` host (127.0.0.1)
* The server name is ``maprdemo``
* The web manager is on [http://maprdemo:8443]() mapr/mapr
* My MapR current version is v. 5.1.0.37549.GA

* Configurer la sandbox en mode accès par pont au lieu du réseau privé (des fois démarrer 2 fois la sandbox pour qu'elle marche)

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

* Configure the **core-site.xml** in the resources directory (to connect correctly to HDFS with Spark). We nedd the following properties :
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
hadoop fs -fs maprfs://my.cluster.com -mkdir -p /user/spark
hadoop fs -fs maprfs://my.cluster.com -put /opt/mapr/spark/spark-1.5.2/lib/spark-assembly-1.5.2-mapr-1602-hadoop2.7.0-mapr-1602.jar /user/spark/spark-assembly.jar
hadoop fs -fs maprfs://my.cluster.com -put /tmp/sparkYarnMapR-1.0-SNAPSHOT-worker.jar /user/spark/sparkYarnMapR-1.0-SNAPSHOT-worker.jar
hadoop fs -fs maprfs://my.cluster.com -chmod -R 777 /user/spark
hadoop fs -fs maprfs://my.cluster.com -ls /user/spark/



hadoop fs -fs maprfs://demo.mapr.com -mkdir -p /user/spark
hadoop fs -fs maprfs://demo.mapr.com -rm /user/spark/spark-assembly.jar
hadoop fs -fs maprfs://demo.mapr.com -put /opt/mapr/spark/spark-1.5.2/lib/spark-assembly-1.5.2-mapr-1602-hadoop2.7.0-mapr-1602.jar /user/spark/spark-assembly.jar
hadoop fs -fs maprfs://demo.mapr.com -rm /user/spark/sparkYarnMapR-1.0-SNAPSHOT-worker.jar
hadoop fs -fs maprfs://demo.mapr.com -put /tmp/sparkYarnMapR-1.0-SNAPSHOT-worker.jar /user/spark/sparkYarnMapR-1.0-SNAPSHOT-worker.jar
hadoop fs -fs maprfs://demo.mapr.com -chmod -R 777 /user/spark
hadoop fs -fs maprfs://demo.mapr.com -ls /user/spark/
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

 * Configuration Hadoop Faire dans le répertoire /etc/hadoop/conf du poste de dev:
   ```
    scp -P 2222 mapr@maprdemo:///opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop/* /etc/hadoop/conf/
   ```


* Launch the Test Java application



## Aide MAPR
Installation du maprClient sur les Edge nodes.
Poru gérer correctement le système de ficheir MaprFS, il faut installer le mapr-client sur le poste lancant l'application JAVA.

Pour cela : 
* Les clés publiques MapR : [clés publiques Mapr](http://maprdocs.mapr.com/51/index.html#AdvancedInstallation/InstallingMapRSoftware-St_26982447-d3e1209.html)

* Les repository MapR: [repository Mapr](http://maprdocs.mapr.com/51/index.html#AdvancedInstallation/AddingMapRreposUbuntu.html)

  La version actuelle se configure comme suit :
  ```
  deb http://package.mapr.com/releases/v5.1.0/ubuntu/ mapr optional
  deb http://package.mapr.com/releases/ecosystem-5.x/ubuntu binary/
  ```
* Le MaprClient: [client Mapr](http://maprdocs.mapr.com/51/index.html#AdvancedInstallation/SettingUptheClient-ubuntu_26982445-d3e544.html)

* Configuration des CLDB nodes : 
  ```
  sudo /opt/mapr/server/configure.sh -N demo.mapr.com -c -C maprdemo:7222 -HS maprdemo
  ```
  
  






