package org.motechproject.tujiokowe.util.serializer;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.motechproject.tujiokowe.domain.Subject;

public class CustomSubjectSerializer extends JsonSerializer<Subject> {

  @Override
  public void serialize(Subject subject, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    if (subject.getVisits() != null) {
      subject.setVisits(null);
    }
    jsonGenerator.writeObject(subject);
  }
}
