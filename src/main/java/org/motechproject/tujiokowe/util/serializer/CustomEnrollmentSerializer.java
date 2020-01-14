package org.motechproject.tujiokowe.util.serializer;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.motechproject.tujiokowe.domain.SubjectEnrollments;

public class CustomEnrollmentSerializer extends JsonSerializer<SubjectEnrollments> {

  @Override
  public void serialize(SubjectEnrollments enrollment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    if (enrollment != null) {
      jsonGenerator.writeString(enrollment.getStatus().getValue());
    } else {
      jsonGenerator.writeString("");
    }
  }
}
