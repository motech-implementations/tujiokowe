package org.motechproject.tujiokowe.helper;

import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.domain.Config;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.exception.IvrException;
import org.motechproject.tujiokowe.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class IvrHelper {

  @Autowired
  private ConfigService configService;

  private final RestTemplate restTemplate = new RestTemplate();

  public String createSubscriber(Subject subject) {
    if (StringUtils.isBlank(subject.getPhoneNumber())) {
      return null;
    }

    Config config = configService.getConfig();
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(config.getIvrUrl() + TujiokoweConstants.SUBSCRIBERS_URL);

    builder.queryParam(TujiokoweConstants.API_KEY, config.getApiKey());
    builder.queryParam(TujiokoweConstants.PHONE, subject.getPhoneNumber());
    builder.queryParam(TujiokoweConstants.SUBJECT_ID_PROPERTY, subject.getSubjectId());
    builder.queryParam(TujiokoweConstants.PREFERRED_LANGUAGE, config.getIvrLanguageId());
    builder.queryParam(TujiokoweConstants.RECEIVE_SMS, "1");
    builder.queryParam(TujiokoweConstants.RECEIVE_VOICE, "1");

    if (StringUtils.isNotBlank(subject.getName())) {
      builder.queryParam(TujiokoweConstants.NAME_PROPERTY, subject.getName());
    }

    return sendIvrRequest(builder, HttpMethod.POST);
  }

  public String updateSubscriber(Subject subject) {
    if (StringUtils.isBlank(subject.getPhoneNumber())) {
      return null;
    }

    if (StringUtils.isBlank(subject.getIvrId())) {
      return createSubscriber(subject);
    }

    Config config = configService.getConfig();
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(config.getIvrUrl()
        + TujiokoweConstants.SUBSCRIBERS_URL + "/" + subject.getIvrId());

    builder.queryParam(TujiokoweConstants.API_KEY, config.getApiKey());
    builder.queryParam(TujiokoweConstants.PHONE, subject.getPhoneNumber());

    if (StringUtils.isNotBlank(subject.getName())) {
      builder.queryParam(TujiokoweConstants.NAME_PROPERTY, subject.getName());
    }

    return sendIvrRequest(builder, HttpMethod.PUT);
  }

  private String sendIvrRequest(UriComponentsBuilder builder, HttpMethod method) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    HttpEntity<?> request = new HttpEntity<>(headers);

    try {
      ResponseEntity<VotoResponseDto> responseEntity = restTemplate.exchange(builder.build().toString(),
          method, request, VotoResponseDto.class);

      if (!HttpStatus.CREATED.equals(responseEntity.getStatusCode()) && !HttpStatus.OK.equals(responseEntity.getStatusCode())) {
        String message = "Invalid IVR service response: " + responseEntity.getStatusCode();
        if (responseEntity.getBody() != null && responseEntity.getBody().getMessage() != null) {
          message = message + ", Response body: " + responseEntity.getBody().getMessage();
        }

        throw new IvrException(message);
      }

      return responseEntity.getBody().getData();
    } catch (RestClientException ex) {
      String message = "Error occurred when sending request to IVR service: " + ex.getMessage();
      throw new IvrException(message, ex);
    }
  }
}
