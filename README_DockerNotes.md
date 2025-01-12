Create a bus app image and Creating a mock-up environment

I am following the references here:

https://spring.io/guides/gs/spring-boot-docker

https://docs.docker.com/compose/intro/compose-application-model/

If you have error like `permission denied` when using `docker`, put a `sudo docker` to run it. 

# Step 1: Package the project into JAR

Do a maven package at the bus-api directory, where the `pom.xml` is installed. When the package is done manually here, we can check for any errors. For example, when the tests fail, we can see it.

Since the UserAccess test still have errors, we will run the package with instruction to skip tests.

Run this:
`./mvnw package -DskipTests`

Notice that in this step, the **application.properties** is still using the **localhost:5432**, our previously installed postgres database.


```
spring.datasource.url=jdbc:postgresql://localhost:5432/bus_api
```

# Step 2: Test the JAR file

Do a test on the packaged JAR file. You can test the endpoints here. This is to make sure that the JAR file is working properly.

Run this:
`java -jar target/<JAR FILE NAME>.jar`

For example, the target directory now has the file **target/bus-api-0.0.1-SNAPSHOT.jar**:

`java -jar target/bus-api-0.0.1-SNAPSHOT.jar`

You can run a few tests with the endpoints to make sure things are fine.

# Step 3: Create the docker build file.

Create a file name **Dockerfile** in the directory where **pom.xml** is located.

```
FROM openjdk:17-jdk-bullseye
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

# Step 4: Build Dockerfile

Build the Dockerfile to get the image.

Run this:
`docker build -t bus-image1:v0 .`

Here I named the image **bus-image1** with tag **v0**.

You can check the image:

```
$ docker image ls
bus-image1   v0  a1bbefbc77dd   54 minutes ago   736MB
```

# Step 5: Test the image

Now, at this stage, the docker image should be able to be port over for use in production deployment.

You can test the image is working correctly by running the image.

## Using locally installed database server:

Run this command: Do NOT put the `-p 8080:8080` because the container is running on the same network as the host.

We need to run the docker container on the `host` network like the command above.

```
$ docker run --rm -it --network=host bus-image1:v0
```

The container can work with the local database--the one that we install in our 
local laptop. 

```
spring.datasource.url=jdbc:postgresql://localhost:5432/bus_api
```

References:

- [Host network driver](https://docs.docker.com/engine/network/drivers/host/)
- [Networking using the host network](https://docs.docker.com/engine/network/tutorials/host/)

You can test with 

```
$ curl '172.21.0.2:8080/app/arrival?BusStopCode=05631'

$ curl '172.21.0.2:8080/app/arrival?BusStopCode=05631&ServiceNo=121'

```

# Step 6: Docker compose to create mock up environments

Docker compose is used to create a mock up environments. Here the app container part is named **frontend** and the postgres server **backend**.

## application.properties

To use the Docker compose, the **application.properties** has to use the hostname of the container: **backend**

Change the postgres **hostname** in **appplication.properties**.

```
spring.datasource.url=jdbc:postgresql://backend:5432/bus_api
```

## Package the JAR

Notice that the **application.properties** is changed, we need to package the JAR file again (Step 1).

Run this:

```
./mvnw package -DskipTests
```

Optional to test the JAR file again (Step 2).

## Create the compose file

Create the file **docker-compose.yaml** in the same directory as the **Dockerfile** and the **pom.xml**:

```
services:
  frontend:
    image: bus-image1:v0
    ports:
      - "8080:8080"
    networks:
      - front-tier
      - back-tier
  backend:
    image: postgres:16-bullseye
    restart: always
    # set shared memory limit when using docker-compose
    shm_size: 128mb
    volumes:
      - ./image-data:/var/lib/postgresql/data
    ports:
      - "5454:5432"
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: bus_api
    networks:
      - back-tier
networks:
  front-tier: {}
  back-tier: {}

```

For the **frontend** app:

-  host port 8080, container port 8080

For the **backend** postgres server:

-  host port 5454 to the container port 5432

I used host port 5454 is because the postgres **backend** will bind to the local port of 5454 and the container **backend** is listening at 5432. The container **frontend** will talk to the **backend** at 5432 in this mock-up environment.

Please note that this **composed** environment is self-contained. It only deals with the container image **bus-image1:v0** and the image **postgres:16-bullseye**. It has nothing to do with our previously installed postgres database.


## Run the compose

```
docker compose up

docker compose up --build
```

You can use `--build` option if you have previously made changes to the images. According to [docker compose](https://docs.docker.com/reference/cli/docker/compose/up/) the option `build`: Build images before starting containers.

Now, if everything is running fine, we will have a mock up postgres server with a persistent volume. 

## Test the compose

### Without any database

We can test with a pull of BusArrival:

```
curl 'localhost:8080/app/arrival?BusStopCode=05631&ServiceNo=196'

```

### With database

We can start pulling data from Datamall. The data will be persisted in the volume.

For example to pull the BusServices data:

```
curl 'localhost:8080/datamall/services'

```

Next, do a test with the services endpoint:

```
$ curl 'localhost:8080/app/services'

$ curl 'localhost:8080/app/services/12'

```

## Stop the compose

When you are done testing, you can stop and remove the composed environment:

```
docker compose down
```

The End

