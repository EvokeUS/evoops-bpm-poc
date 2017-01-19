retry() {
    local -r -i max_attempts="5"; shift
    local -r cmd="$@"
    local -i attempt_num=1
	
	callURL $1
	retval=$?
    until [ $retval != 1 ]
    do
        if (( attempt_num == max_attempts ))
        then
            echo "Attempt $attempt_num failed and there are no more attempts left!"
			return 1
        else
            echo "Attempt $attempt_num failed! Trying again in $attempt_num seconds..."
			callURL $1
            (( attempt_num++ ))
        fi
    done
}

callURL(){
	url=$1
    local -r res=`curl -i -X POST -H "Content-Type:application/json" $url -d '{"url":"http://192.168.99.100:8484/bonita", "userName":"walter.bates", "pwd":"bpm", "technicalUser":"install", "technicalPwd":"install", "organizationFile":"/opt/tomcat/Organization_Data.xml", "bdmFile":"/opt/tomcat/bdm.zip", "barFile":"/opt/tomcat/SimpleProcess--1.0.bar", "processName":"SimpleProcess"}' | grep HTTP/1.1 | awk {'print $2'}` 
	echo '---'
	echo $res
	echo '---'
	
	if [ $res != 200 ]; then		
		return 1
	else 
		echo "Success"
		return 0
	fi		
}

START_TIME=$SECONDS
echo '-------------------------------------'
echo 'Verify Bonita'
echo '-------------------------------------'
retry 5 'http://192.168.99.100:8181/hr-rest/bpm/verify'

echo '-------------------------------------'
echo '-----------Setting up bonita environment------'
echo 'Install Organisation: Importing users'
retry 5 'http://192.168.99.100:8181/hr-rest/bpm/installOrganization'

echo ' '
echo '-------------------------------------'
echo 'Install Organisation: Importing users'
retry 5 'http://192.168.99.100:8181/hr-rest/bpm/installOrganization'
echo '---'
echo 'Update Profile: Assign walter.bates to Administrator and User profiles'
retry 5 'http://192.168.99.100:8181/hr-rest/bpm/updateProfile'

echo ' '
echo '-------------------------------------'
echo 'Install Organisation: Importing users'
retry 5 'http://192.168.99.100:8181/hr-rest/bpm/installOrganization'
echo '---'
echo 'Update Profile: Assign walter.bates to Administrator and User profiles'
retry 5 'http://192.168.99.100:8181/hr-rest/bpm/updateProfile'
echo 'Install BDM'
retry 5 'http://192.168.99.100:8181/hr-rest/bpm/installBDM'

echo ' '
echo '-------------------------------------'
echo 'Install Process'
retry 5 'http://192.168.99.100:8181/hr-rest/bpm/installProcess'
echo '-------------------------------------'

ELAPSED_TIME=$(($SECONDS - $START_TIME))
echo "---------------------------------------------------------------"
echo "Time taken for Bonita Setup: $(($ELAPSED_TIME/60)) min $(($ELAPSED_TIME%60)) sec"   
echo "---------------------------------------------------------------"


	
