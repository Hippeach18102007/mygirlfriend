# --- Giai đoạn 1: Build ứng dụng ---
# Sử dụng một image chứa Java Development Kit (JDK) và Maven để build code
FROM eclipse-temurin:17-jdk-jammy as builder

# Đặt thư mục làm việc bên trong image
WORKDIR /app

# Copy file pom.xml trước để tận dụng Docker cache cho các dependency
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ source code
COPY src ./src

# Chạy lệnh build của Maven
# Lệnh này sẽ biên dịch code và đóng gói thành file .jar
RUN mvn clean install -DskipTests


# --- Giai đoạn 2: Run ứng dụng ---
# Sử dụng một image chỉ chứa Java Runtime Environment (JRE), nhỏ gọn hơn nhiều
FROM eclipse-temurin:17-jre-jammy

# Đặt thư mục làm việc
WORKDIR /app

# **QUAN TRỌNG**: Copy file .jar đã được build từ giai đoạn 'builder'
# Hãy chắc chắn tên file 'ny-0.0.1-SNAPSHOT.jar' khớp với file trong thư mục 'target' của bạn
COPY --from=builder /app/target/ny-0.0.1-SNAPSHOT.jar app.jar

# Báo cho Docker biết ứng dụng sẽ chạy ở port 9000
EXPOSE 9000

# Lệnh để khởi chạy ứng dụng khi container bắt đầu
ENTRYPOINT ["java", "-jar", "app.jar"]