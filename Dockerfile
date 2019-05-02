FROM openjdk:10

# Add the workspace and make working directory.
ADD . /elkd/
WORKDIR /elkd/

# Build with gradle wrapper and install distribution
RUN ./gradlew assembleDist installDist

# Set this as new working directory; docker compose will be basing
# itself in this directory.
WORKDIR /elkd/core/build/install/elkd-server
