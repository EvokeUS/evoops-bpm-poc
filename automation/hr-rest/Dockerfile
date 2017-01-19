FROM dordoka/tomcat
ADD hr-rest.war /opt/tomcat/webapps/
ADD Organization_Data.xml /opt/tomcat
ADD SimpleProcess--1.0.bar /opt/tomcat
ADD bdm.zip /opt/tomcat

EXPOSE 8181
CMD ["catalina.sh", "run"]