package org.motechproject.tujiokowe.util.serializer;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class CustomBooleanSerializer extends JsonSerializer<Boolean> {

  @Override
  public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializerProvider)
      throws IOException {
    if (value == null) {
      gen.writeString("");
    } else if (value) {
      gen.writeString("Yes");
    } else {
      gen.writeString("No");
    }
  }
}
