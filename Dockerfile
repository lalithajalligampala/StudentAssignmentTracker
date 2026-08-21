FROM tomcat:9.0-jdk17-temurin

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Create required directories
RUN mkdir -p /usr/local/tomcat/webapps/ROOT
RUN mkdir -p /app/web/WEB-INF/classes

# Copy project files
COPY src /app/src
COPY web /app/web
COPY lib /app/lib

# Compile Java Servlet files
RUN javac \
    -cp "/usr/local/tomcat/lib/*:/app/lib/*:/app/web/WEB-INF/lib/*" \
    -d "/app/web/WEB-INF/classes" \
    $(find /app/src -name "*.java")

# Copy application into Tomcat ROOT
RUN cp -r /app/web/* /usr/local/tomcat/webapps/ROOT/

# Start Tomcat using Render's PORT
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT}\\\"/\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]

