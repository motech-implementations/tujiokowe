package org.motechproject.tujiokowe.util.serializer;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Serializer for DateTime representation in UI
 */
public class CustomDateTimeSerializer extends JsonSerializer<DateTime> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  @Override
  public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg)
      throws IOException {
    gen.writeString(FORMATTER.print(value));
  }
}
