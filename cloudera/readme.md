#Spark on Cloudera


### Installation
* Installation de VirtualBox
* Téléchargement de CDH sandbox [http://www.cloudera.com/downloads/quickstart_vms/5-7.html]()
* Importation de la VM dans VirtualBox (en remettant à zéro les adresses Mac - Mettre au moins 8Go de RAM et 2 CPUs)
* Lancement de la VM, puis dans la VM : "Launch Cloudera Express"
* Configurer le /etc/hosts de la machine parente pour relier quickstart.cloudera à 127.0.0.1 (puis sudo service networking restart)

Actuellement je suis :
CDH 5.7.0 et spark est en version 1.6.0+cdh5.7.0+180


### Cloudera from Scratch
* Ubuntu 14.04 (Nom de machine CDH par exemple)
* 8Go de RAM Minimum - 4 CPU - Accès réseau par pont
* Lors de l'installation installer openssh-server (sinon ``apt-get install openssh-server``)
* Vérification de l'ip dans /etc/hosts
* ``sudo passwd root`` et mettre le password ``root``
* vi ``/etc/ssh/sshd_config``
* Modifier/Ajouter la ligne ``PermitRootLogin yes``
* Commenter la ligne ``StrictModes yes``
* Modifier/Ajouter la ligne ``PasswordAuthentication yes``
* ``sudo service ssh reload``
* Test connection ssh root@CDH
* ``
wget https://archive.cloudera.com/cm5/installer/latest/cloudera-manager-installer.bin;
chmod u+x cloudera-manager-installer.bin;
sudo ./cloudera-manager-installer.bin;
``



### Usage

* Aller sur la page [http://quickstart.cloudera:7180]() et se connecter (cloudera/cloudera)
* Aller dans Clusters / YARN (MR2 Included) puis dans Actions / Télécharger la configuration cliente
* Extraire sont contenu dans le répertoire src/main/resources
* Compiler le projet avec Maven ```mvn clean install```
* Copie du worker précédemment compilé surle cluster Hadoop et dans HDFS.
  * Comme je n'arrive pas à me connecter en SSH sur la sandbox, je le fais via hue [http://quickstart.cloudera:8888]() (Rq, ne pas oublier de donner les droits à tout le monde).
  * Le spark assembly est dans le répertoire (dans la sandbox): ```/usr/lib/spark/lib/spark-assembly.jar```
```
scp target/sparkYarnCDH-1.0-SNAPSHOT-worker.jar root@cdh:///tmp/
```
Sur la machine CDH
```
sudo -u spark hdfs dfs -mkdir -p /user/spark
sudo -u spark hdfs dfs -put /opt/cloudera/parcels/CDH/lib/spark/lib/spark-assembly.jar /user/spark/spark-assembly.jar
sudo -u spark hdfs dfs -put /tmp/sparkYarnCDH-1.0-SNAPSHOT-worker.jar /user/spark/sparkYarnCDH-1.0-SNAPSHOT-worker.jar
sudo -u spark hdfs dfs -chmod -R 777 /user/spark
sudo -u spark hdfs dfs -ls /user/spark/
```

Pour avoir le nom du cluster pour configurer les URIs hdfs://XXX lire dans topology.map


## Résolution des soucis

* Etat des applications : [http://quickstart.cloudera:8088/cluster/apps]()
* Avoir les logs Yarn : ```yarn logs -applicationId XXX```
* Si configuration de spark_local_ip aller le faire sur le CDH : Dans ```/usr/lib/spark/conf/spark-env.sh```
// J'ai bouriné en le mettant dans /usr/lib/spark/bin/setenv.
Rajouter export ```SPARK_LOCAL_IP=127.0.0.1```

* SparkLocalIp de spark à changer dans [http://www.cloudera.com/documentation/enterprise/5-5-x/topics/spark_applications_configuring.html#concept_h1m_sfl_2s]()

* Si l'application Yarn reste en mode Accepted : [https://community.cloudera.com/t5/Batch-Processing-and-Workflow/JOB-Stuck-in-Accepted-State/td-p/29494]()
* Si une erreur du type : Initial job has not accepted any resources; check your cluster UI to ensure that workers are registered and have sufficient resources
 Changer le maximum à 2 (dans la conf Yarn)
  <property>
    <name>yarn.scheduler.maximum-allocation-vcores</name>
    <value>2</value>
  </property>

* Pour éviter les warnings : Conf HDFs:  dfs.replication à 1

* Si erreur check your cluster UI to ensure that workers are registered and have sufficient resources :
  <property>
    <name>mapreduce.map.memory.mb</name>
    <value>128</value>
  </property>
  <property>
    <name>mapreduce.reduce.memory.mb</name>
    <value>128</value>
  </property>


* Configurer les VCores : Aller dans Cluster / Dynamic Resource Pool Configuration
    Mettre le total of vCore et memory à 4 au moins et 2.4GiB
