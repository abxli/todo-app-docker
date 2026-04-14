FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Install JavaFX
RUN apt-get update && apt-get install -y openjfx

# Copy source code
COPY todo-app/src/main/java /app

# Compile with JavaFX module path
RUN javac \
  --module-path /usr/share/openjfx/lib \
  --add-modules javafx.controls,javafx.fxml \
  app/TodoApplication.java

# Run with JavaFX module path
CMD ["java", "--module-path", "/usr/share/openjfx/lib", "--add-modules", "javafx.controls,javafx.fxml", "-cp", ".", "app.TodoApplication"]