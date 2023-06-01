cd ..

./gradlew build --x test

docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)  || true

docker image rm inqueue/wait-queue-service:loadtest

docker image build -t inqueue/wait-queue-service:loadtest .

docker-compose -f ./docker-compose.yml up
