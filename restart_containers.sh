echo 'Restarting Docker Containers ...'
docker restart bpm-oracle11g

sleep 20s
docker restart bpm-engine bpm-web bpm-rest bpm-mongo


