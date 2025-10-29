FROM tomcat:11.0-jakarta

# Remove default apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy your WAR file into Tomcat
COPY Agri-Waste_Trading_Website.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
