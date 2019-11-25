package org.motechproject.tujiokowe.util.serializer;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.motechproject.tujiokowe.domain.enums.VisitType;

/**
 * Deserializer for VisitType representation in UI
 */
public class CustomVisitTypeDeserializer extends JsonDeserializer<VisitType> {

  @Override
  public VisitType deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    String typeString = parser.getText();
    VisitType visitType = VisitType.getByValue(typeString);
    if (visitType == null) {
      visitType = VisitType.valueOf(typeString);
    }
    return visitType;
  }
}
