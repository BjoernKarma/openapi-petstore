package de.openapi.petstore;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import java.util.concurrent.CompletableFuture;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  public static void main(final String[] args) {
    // TODO: Only for testing purposes, because no Mongo DBaaS is available
    CompletableFuture.runAsync(() -> {
      MongoServer server = new MongoServer(new MemoryBackend());
      server.bind("localhost", 27017);
    });
    SpringApplication.run(Application.class, args);
  }
}
