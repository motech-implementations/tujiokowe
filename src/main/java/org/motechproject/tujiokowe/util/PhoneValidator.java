package org.motechproject.tujiokowe.util;

import org.apache.commons.lang3.StringUtils;

public class PhoneValidator {

  private static final int MIN_PHONE_LENGTH = 8;

  public static boolean isValid(String phoneNumber) {
    return StringUtils.isNotBlank(phoneNumber) && phoneNumber.length() >= MIN_PHONE_LENGTH;
  }

  public static boolean isNotValid(String phoneNumber) {
    return !isValid(phoneNumber);
  }
}
