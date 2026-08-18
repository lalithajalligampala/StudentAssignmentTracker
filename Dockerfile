FROM tomcat:9.0-jdk17-temurin

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy project files
COPY src /app/src
COPY web /app/web
COPY lib /app/lib

# Create classes directory
RUN mkdir -p /app/web/WEB-INF/classes

# Compile Java Servlet source files
RUN javac \
    -cp "/usr/local/tomcat/lib/*:/app/lib/*" \
    -d "/app/web/WEB-INF/classes" \
    $(find /app/src -name "*.java")

# Deploy application as ROOT
RUN cp -r /app/web/* /usr/local/tomcat/webapps/ROOT/

# Render provides the PORT environment variable
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT}\\\"/\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]