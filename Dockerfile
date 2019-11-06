FROM openjdk:11

# Add the workspace and make working directory.
ADD . /kerala/
WORKDIR /kerala/

# Build with gradle wrapper and install distribution
RUN ./gradlew assembleDist installDist

# Set this as new working directory; docker compose will be basing
# itself in this directory.
WORKDIR /kerala/core/build/install/kerala-server
