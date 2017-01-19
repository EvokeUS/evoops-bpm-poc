echo 'Building hr-rest and hr-web artifacts ...'
cd source/pom-master
mvn clean install

cd ..
cd hr-web
mvn clean package

cd ..
cd hr-rest
mvn -DskipTests=true clean package

cd ..
cd ..

echo 'Copying artifacts ...'
pwd
cp -r source/hr-web/target/hr-web.war automation/hr-web
cp -r source/hr-rest/target/hr-rest.war automation/hr-rest