FROM openjdk:11
COPY backend/target/universal/stage /root/app
WORKDIR /root/app
CMD ["./bin/backend"]
