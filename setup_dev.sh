START_TIME=$SECONDS

echo 'Starting Bonita and Oracle ...'
sh automation/bonita/start_bpm_oracle.sh

echo 'Removing HR-Web and HR-Rest ...'
docker-compose -f automation/docker-compose.yml down

echo 'Starting HR-Web and HR-Rest ...'
docker-compose -f automation/docker-compose.yml up --build -d 

docker commit bpm-engine bonita-community:7.3.3

echo 'All docker containers started!!!'

echo 'Setting up Bonita Environment'
sh automation/bonita/setup_bonita_env.sh
echo 'Bonita Portal: http://192.168.99.100:8484/bonita/'
echo 'HR Web	   : http://192.168.99.100:8989/hr-web'

ELAPSED_TIME=$(($SECONDS - $START_TIME))
echo " "
echo " "
echo "---------------------------------------------------------------"
echo "Total time taken for Inital Setup: $(($ELAPSED_TIME/60)) min $(($ELAPSED_TIME%60)) sec"   
echo "---------------------------------------------------------------"