package de.openapi.petstore.converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {
    @Override
    public OffsetDateTime convert(Date date) {
        return date != null ? OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()) : null;
    }
}
