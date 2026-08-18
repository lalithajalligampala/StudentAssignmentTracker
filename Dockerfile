# Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy project files
COPY src /app/src
COPY web /app/web
COPY lib /app/lib

# Create classes directory
RUN mkdir -p /app/web/WEB-INF/classes

# Compile Java Servlet files
RUN javac \
    -cp "/app/lib/*" \
    -d /app/web/WEB-INF/classes \
    $(find /app/src -name "*.java")

# Runtime stage
FROM tomcat:9.0-jdk17-temurin

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy application
COPY --from=build /app/web /usr/local/tomcat/webapps/StudentAssignmentTracker

# Render provides the PORT environment variable
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT}\\\"/\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]