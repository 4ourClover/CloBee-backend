## 1. 경량 JDK 이미지 기반
#FROM eclipse-temurin:21-jdk-jammy
#
## 2. JAR 파일 복사 (빌드한 파일명에 따라 수정 필요)
#COPY clobee-0.0.1-SNAPSHOT.jar app.jar
#
## 3. 포트 노출 (필요에 따라 수정)
#EXPOSE 8080
#
## 4. 실행 명령어
#ENTRYPOINT ["java", "-jar", "app.jar"]

# === Base: Java with Nginx and OpenSSL ===
FROM eclipse-temurin:21-jdk-jammy

# Install nginx + openssl
RUN apt-get update && apt-get install -y nginx openssl && rm -rf /var/lib/apt/lists/*

# --- 1. 앱 실행용 디렉토리 ---
WORKDIR /app

# Copy JAR
COPY clobee-0.0.1-SNAPSHOT.jar /app/app.jar

# --- 2. Nginx 설정 및 SSL 인증서 경로 ---
RUN mkdir -p /etc/ssl/certs /etc/ssl/private

# Self-signed 인증서 생성
RUN openssl req -x509 -nodes -days 365 \
  -newkey rsa:2048 \
  -keyout /etc/ssl/private/nginx.key \
  -out /etc/ssl/certs/nginx.crt \
  -subj "/CN=localhost"

# --- 3. Nginx 설정 파일 생성 ---
RUN echo '\
server {\n\
    listen 443 ssl;\n\
    server_name localhost;\n\
\n\
    ssl_certificate /etc/ssl/certs/nginx.crt;\n\
    ssl_certificate_key /etc/ssl/private/nginx.key;\n\
\n\
    location / {\n\
        proxy_pass http://localhost:8080/;\n\
        proxy_set_header Host $host;\n\
        proxy_set_header X-Real-IP $remote_addr;\n\
    }\n\
}' > /etc/nginx/sites-available/default

# Remove default site and enable ours
RUN ln -sf /etc/nginx/sites-available/default /etc/nginx/sites-enabled/default

# --- 4. 포트 노출 ---
EXPOSE 443

# --- 5. CMD: Spring Boot 실행 후 nginx 실행 ---
CMD java -jar /app/app.jar & nginx -g 'daemon off;'
