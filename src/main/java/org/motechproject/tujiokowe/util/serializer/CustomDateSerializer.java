package org.motechproject.tujiokowe.util.serializer;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CustomDateSerializer extends JsonSerializer<LocalDate> {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormat.forPattern("yyyy-MM-dd");

  @Override
  public void serialize(LocalDate value, JsonGenerator gen,
      SerializerProvider arg2)
      throws IOException {
    gen.writeString(FORMATTER.print(value));
  }
}

