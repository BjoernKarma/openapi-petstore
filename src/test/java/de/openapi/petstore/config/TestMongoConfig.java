package de.openapi.petstore.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import de.openapi.petstore.converter.Converters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {
    "de.openapi.petstore"}, mongoTemplateRef = "mongoTemplate")
@Profile({"testMongo"})
public class TestMongoConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestMongoConfig.class);

  @Primary
  @Bean(name = "mongoTemplate")
  public MongoTemplate mongoTemplate(MongoClient mongoClient) {
    LOGGER.info("mongoTemplate");
    MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(mongoClient));
    MappingMongoConverter conv = (MappingMongoConverter) mongoTemplate.getConverter();
    // tell mongodb to use the custom converters
    conv.setCustomConversions(new Converters().mongoCustomConversions());
    conv.afterPropertiesSet();
    return mongoTemplate;
  }

  public SimpleMongoClientDatabaseFactory mongoDbFactory(MongoClient mongoClient) {
    return new SimpleMongoClientDatabaseFactory(mongoClient, "pet-store-test");
  }

  @Bean(destroyMethod = "shutdown")
  public MongoServer mongoServer() {
    MongoServer mongoServer = new MongoServer(new MemoryBackend());
    mongoServer.bind();
    return mongoServer;
  }

  @Bean(destroyMethod = "close")
  public MongoClient mongoClient(MongoServer mongoServer) {
    return MongoClients.create("mongodb:/" + mongoServer.getLocalAddress());
  }

}


