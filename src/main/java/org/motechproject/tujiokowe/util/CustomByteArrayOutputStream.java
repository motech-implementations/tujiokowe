package org.motechproject.tujiokowe.util;

import java.io.ByteArrayOutputStream;

public class CustomByteArrayOutputStream extends ByteArrayOutputStream {

  public synchronized byte[] readDataAndReset() {
    byte[] data = toByteArray();
    reset();

    return data;
  }
}
