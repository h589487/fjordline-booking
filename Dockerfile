# Steg 1: Bruk et lettvekts Java-bilde
FROM eclipse-temurin:17-jdk-alpine

# Steg 2: Opprett arbeidsmappe
WORKDIR /app

# Steg 3: Kopier den ferdigbygde jar-filen fra target-mappen
# (Husk å kjøre 'mvn clean package' først!)
COPY target/*.jar app.jar

# Steg 4: Start applikasjonen
ENTRYPOINT ["java", "-jar", "app.jar"]

# Eksponer porten Spring Boot bruker
EXPOSE 8080