package org.motechproject.tujiokowe.util.serializer;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class CustomBooleanDeserializer extends JsonDeserializer<Boolean> {

  @Override
  public Boolean deserialize(JsonParser jp, DeserializationContext deserializationContext)
      throws IOException {
    String value = jp.getText();

    if ("Yes".equalsIgnoreCase(value)) {
      return true;
    }

    if ("No".equalsIgnoreCase(value)) {
      return false;
    }

    return null;
  }
}
