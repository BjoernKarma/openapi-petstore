package de.openapi.petstore;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class MongoH2StandaloneApplication {

  public static void main(final String[] args) {
    MongoServer server = new MongoServer(new MemoryBackend());
    server.bind("localhost", 27017);
  }
}
