# Replicated-log

This repository contains a replicated log system implemented using the Master-Secondary architecture.
The Master and Secondary components are built using the Spring framework to provide a simple HTTP server for communication,
and the GRPC protocol is used for efficient message transfer between the components.

## Features

- Replicated log system with a Master and multiple Secondary components
- HTTP server for easy communication and message retrieval
- GRPC protocol for efficient message transfer between components
- Customizable delay for simulating message processing time

## Build

To build the project, follow these steps:

1. Clone the repository:

   ```shell
   git clone https://github.com/KnyshBohdan/replicated-log
   cd replicated-log
   ```
   
2. Create the package using Maven:

   ```shell
   mvn clean package
   ```
   
3. Build the Docker images and start the containers using Docker Compose:

   ```shell
   docker-compose up --build
   ```

This command will create three servers: one Master and two Secondary servers.

## Usage

### Posting message

To post a message to the Master server, use the following command:

```shell
curl -X POST -H "Content-Type: application/json" -d '{"content":"Hello, World!"}' http://localhost:8080/messages
```

The message will be replicated to the Secondary servers with a configurable delay.
By default, both Secondary servers have a delay of 5 seconds, so the complete message transfer process may take up to 10 seconds.

### Retrieving Messages

To retrieve messages from the Master server, use the following command:

```shell
curl http://localhost:8080/messages
```

To retrieve messages from the first Secondary server, use the following command:

```shell
curl http://localhost:8081/messages
```

To retrieve messages from the second Secondary server, use the following command:

```shell
curl http://localhost:8082/messages
```
