START_TIME=$SECONDS
echo 'Removing Docker Containers ...'
docker rm -f bpm-oracle11g bpm-engine bpm-web bpm-rest bpm-mongo

echo 'Starting Oracle 11g ...'
docker run -d --name bpm-oracle11g -p 4555:22 -p 49162:1521 wnameless/oracle-xe-11g:14.04.4

sleep 20s
echo 'Starting Bonita Engine ...'
docker run -v /data:/opt/bonita --name bpm-engine -d -p 8484:8080 bonita-community:7.3.3

echo 'Starting HR-Web and HR-Rest ...'
docker-compose -f automation/docker-compose.yml up -d 

sleep 10s
echo 'Setting up Bonita Environment'
sh automation/bonita/setup_bonita_env.sh
echo 'Bonita Portal: http://192.168.99.100:8484/bonita/'
echo 'HR Web	   : http://192.168.99.100:8989/hr-web'

ELAPSED_TIME=$(($SECONDS - $START_TIME))
echo "---------------------------------------------------------------"
echo "Total time taken for Recreating containers: $(($ELAPSED_TIME/60)) min $(($ELAPSED_TIME%60)) sec"   
echo "---------------------------------------------------------------"


