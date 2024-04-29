package de.openapi.petstore.converter;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class Converters {
    private static final Logger LOGGER = LoggerFactory.getLogger(Converters.class);

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        LOGGER.info("mongoCustomConversions");
        return new MongoCustomConversions(
                Arrays.asList(
                        new OffsetDateTimeReadConverter(),
                        new OffsetDateTimeWriteConverter()));
    }
}
