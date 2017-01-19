echo "Starting BPM_Task automation"
echo "Deleting containers"

#Remove the containers
docker rm -f bpm-engine
docker rm -f bpm-oracle11g

dos2unix  automation/bonita/setenv.sh
dos2unix  automation/bonita/server.xml
dos2unix  automation/bonita/bitronix-resources.properties
dos2unix  automation/bonita/bonita.xml

#Running bpm-engine container
docker run -v /data:/opt/bonita --name bpm-engine -d -p 8484:8080 bonita

#Running bpm-oracle11g container
docker run -d --name bpm-oracle11g -p 4555:22 -p 49162:1521 wnameless/oracle-xe-11g:14.04.4

sleep 40s

#Copying the required configuration files
docker cp automation/bonita/ojdbc6.jar bpm-engine:/opt/bonita/BonitaBPMCommunity-7.3.3-Tomcat-7.0.67/lib/bonita/ojdbc6.jar
docker cp automation/bonita/setenv.sh bpm-engine:/opt/bonita/BonitaBPMCommunity-7.3.3-Tomcat-7.0.67/bin/setenv.sh
docker cp automation/bonita/server.xml bpm-engine:/opt/bonita/BonitaBPMCommunity-7.3.3-Tomcat-7.0.67/conf/server.xml
docker cp automation/bonita/bitronix-resources.properties bpm-engine:/opt/bonita/BonitaBPMCommunity-7.3.3-Tomcat-7.0.67/conf/bitronix-resources.properties
docker cp automation/bonita/bonita.xml bpm-engine:/opt/bonita/BonitaBPMCommunity-7.3.3-Tomcat-7.0.67/conf/Catalina/localhost/bonita.xml

echo "deleting h2 jar files"
#removing h2 jar files
docker exec bpm-engine rm -rf /opt/bonita/BonitaBPMCommunity-7.3.3-Tomcat-7.0.67/lib/bonita/h2-1.3.170.jar
docker exec bpm-engine rm -rf /opt/bonita/BonitaBPMCommunity-7.3.3-Tomcat-7.0.67/lib/bonita/bonita-tomcat-h2-listener-1.0.1.jar

docker stop bpm-engine
sleep 13s
docker start bpm-engine
#exit the container
