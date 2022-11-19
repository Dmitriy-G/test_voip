### Push image

docker tag "image-name"
docker image push "image-name"

### Pull image and run
docker pull "image-name"
docker run --name "test-voip" -d -p 8080:8080/udp "image-name"

x
