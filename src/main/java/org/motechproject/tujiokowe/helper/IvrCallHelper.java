package org.motechproject.tujiokowe.helper;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.service.OutboundCallService;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.domain.Config;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.VotoMessage;
import org.motechproject.tujiokowe.exception.TujiokoweInitiateCallException;
import org.motechproject.tujiokowe.repository.VotoMessageDataService;
import org.motechproject.tujiokowe.service.ConfigService;
import org.motechproject.tujiokowe.service.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IvrCallHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(IvrCallHelper.class);

  @Autowired
  private ConfigService configService;

  @Autowired
  private VotoMessageDataService votoMessageDataService;

  @Autowired
  private SubjectService subjectService;

  private OutboundCallService outboundCallService;

  public void initiateIvrCall(String messageKey, String externalId) {
    Config config = configService.getConfig();

    Subject subject = getSubject(externalId);

    if (config.getSendIvrCalls() != null && config.getSendIvrCalls()
        && StringUtils.isNotBlank(subject.getIvrId())) {

      String votoMessageId = getVotoMessageId(messageKey, externalId);

      Map<String, String> callParams = new HashMap<>();
      if (StringUtils.isNotBlank(config.getVoiceSenderId())) {
        callParams.put(TujiokoweConstants.VOICE_SENDER_ID, config.getVoiceSenderId());
      }
      if (StringUtils.isNotBlank(config.getSmsSenderId())) {
        callParams.put(TujiokoweConstants.SMS_SENDER_ID, config.getSmsSenderId());
      }
      callParams.put(TujiokoweConstants.API_KEY, config.getApiKey());
      callParams.put(TujiokoweConstants.MESSAGE_ID, votoMessageId);
      callParams.put(TujiokoweConstants.SEND_TO_SUBSCRIBERS, subject.getIvrId());
      callParams.put(TujiokoweConstants.WEBHOOK_URL, config.getStatusCallbackUrl());
      callParams.put(TujiokoweConstants.SEND_SMS_IF_VOICE_FAILS, config.getSendSmsIfVoiceFails() ? "1" : "0");
      callParams.put(TujiokoweConstants.DETECT_VOICEMAIL, config.getDetectVoiceMail() ? "1" : "0");
      callParams.put(TujiokoweConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
      callParams.put(TujiokoweConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
      callParams.put(TujiokoweConstants.RETRY_ATTEMPTS_LONG, TujiokoweConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
      callParams.put(TujiokoweConstants.SUBJECT_ID, externalId);
      callParams.put(TujiokoweConstants.SUBJECT_PHONE_NUMBER, subject.getPhoneNumber());

      LOGGER.info("Initiating call: {}", callParams.toString());

      outboundCallService.initiateCall(config.getIvrSettingsName(), callParams);
    }
  }

  private Subject getSubject(String subjectId) {
    Subject subject = subjectService.findSubjectBySubjectId(subjectId);

    if (subject == null) {
      throw new TujiokoweInitiateCallException(
          "Cannot initiate call, because Provider with id: %s not found", subjectId);
    }

    return subject;
  }

  private String getVotoMessageId(String messageKey, String subjectId) {
    VotoMessage votoMessage = votoMessageDataService.findByMessageKey(messageKey);

    if (votoMessage == null) {
      throw new TujiokoweInitiateCallException(
          "Cannot initiate call for Provider with id: %s, because Voto Message with key: %s not found",
          subjectId, messageKey);
    }

    return votoMessage.getVotoIvrId();
  }

  @Autowired
  public void setOutboundCallService(OutboundCallService outboundCallService) {
    this.outboundCallService = outboundCallService;
  }
}
