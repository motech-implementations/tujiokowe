package org.motechproject.tujiokowe.util.serializer;

import java.io.IOException;
import java.util.List;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.motechproject.tujiokowe.domain.Visit;

public class CustomVisitListSerializer extends JsonSerializer<List<Visit>> {

  @Override
  public void serialize(List<Visit> visits, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    StringBuilder visitsList = new StringBuilder();
    String prefix = "";

    if (visits != null && !visits.isEmpty()) {
      for (Visit visit : visits) {
        visitsList.append(prefix);
        prefix = ", ";
        visitsList.append(visit.toString());
      }
    }

    jsonGenerator.writeString(visitsList.toString());
  }
}
