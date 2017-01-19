#!/bin/bash

retry() {
    local -r -i max_attempts="5"; shift
    local -r cmd="$@"
    local -i attempt_num=1
	
	callURL $1
	retval=$?
    #until $cmd
	echo $retval
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
	echo $0
	url='google.in'
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
