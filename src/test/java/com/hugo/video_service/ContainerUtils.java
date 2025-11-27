package com.hugo.video_service;

import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

public class ContainerUtils {
    public static MongoDBAtlasLocalContainer setUpMongoAtlas(){
        MongoDBAtlasLocalContainer container = new MongoDBAtlasLocalContainer(
                DockerImageName.parse("mongodb/mongodb-atlas-local:7.0.9")
        )
                .withEnv("MONGODB_INITDB_ROOT_USERNAME", "admin")
                .withEnv("MONGODB_INITDB_ROOT_PASSWORD", "password")
                .withEnv("MONGODB_INITDB_DATABASE", "test");

        container.start();
        String uri = String.format(
                "mongodb://admin:password@%s:%d/test?authSource=admin",
                container.getHost(),
                container.getMappedPort(27017)
        );

        System.setProperty("spring.data.mongodb.uri", uri);

        return container;
    }

    public static RabbitMQContainer setUpRabbitMq(){
        RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(
                DockerImageName.parse("rabbitmq:4.2.1-management-alpine")
        )
                .withEnv("RABBITMQ_DEFAULT_USER", "guest")
                .withEnv("RABBITMQ_DEFAULT_PASS", "guest");

        rabbitMQContainer.start();

        System.setProperty("spring.rabbitmq.host", rabbitMQContainer.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitMQContainer.getMappedPort(5672).toString());
        System.setProperty("spring.rabbitmq.username", "guest");
        System.setProperty("spring.rabbitmq.password", "guest");
        System.setProperty("spring.rabbitmq.virtual-host", "/");


        return rabbitMQContainer;
    }
}
