### Push image

docker tag "image-name"
docker image push "image-name"

### Pull image and run
docker pull "image-name"
docker run --name "test-voip" -d -p 3333:3333/udp "image-name"
