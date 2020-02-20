package org.motechproject.tujiokowe.service.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.tujiokowe.client.TujiokoweFtpsClient;
import org.motechproject.tujiokowe.constants.TujiokoweConstants;
import org.motechproject.tujiokowe.domain.Config;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.exception.FtpException;
import org.motechproject.tujiokowe.service.ConfigService;
import org.motechproject.tujiokowe.service.TujiokoweImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("tujiokoweImportService")
public class TujiokoweImportServiceImpl implements TujiokoweImportService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TujiokoweImportServiceImpl.class);

  @Autowired
  private ConfigService configService;

  @Autowired
  private EntityService entityService;

  @Autowired
  private CsvImportExportService csvImportExportService;

  @Autowired
  private SubjectCsvImportCustomizer subjectCsvImportCustomizer;

  @Override
  public void fetchCSVUpdates() {
    fetchCSVUpdates(null);
  }

  @Override //NO CHECKSTYLE CyclomaticComplexity
  public void fetchCSVUpdates(LocalDate startDate) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(TujiokoweConstants.CSV_DATE_FORMAT);
    Config config = configService.getConfig();
    String lastCsvUpdate = config.getLastCsvUpdate();
    LocalDate afterDate = getLastCsvUpdate(lastCsvUpdate, dateTimeFormatter, startDate);

    String hostname = config.getFtpsHost();
    String username = config.getFtpsUsername();
    String password = config.getFtpsPassword();
    String directory = config.getFtpsDirectory();
    String knownHostsFile = config.getKnownHostsFile();

    Integer port = config.getFtpsPort();

    LOGGER.info("Started fetching CSV files modified after {} from {}", afterDate, hostname);

    TujiokoweFtpsClient ftpsClient = new TujiokoweFtpsClient();

    try {
      ftpsClient.connect(knownHostsFile, hostname, port, username, password);
    } catch (FtpException e) {
      LOGGER.error("Could not connect to RAVE FTPS: " + e.getMessage(), e);
      return;
    }
    List<String> filenames;
    try {
      filenames = ftpsClient.listFiles(directory);
    } catch (FtpException e) {
      LOGGER.error("Could not list files: " + e.getMessage(), e);
      return;
    }
    LocalDate lastUpdated = getLastCsvUpdate(lastCsvUpdate, dateTimeFormatter, null);

    for (String filename: filenames) {
      Matcher m = TujiokoweConstants.CSV_FILENAME_PATTERN.matcher(filename);
      if (!m.matches()) {
        LOGGER.error("Skipping " + filename + " because the filename does not match specified format");
      } else {
        try {
          LocalDate date = dateTimeFormatter.parseLocalDate(m.group(1));
          if (date.isAfter(afterDate)) {
            InputStream inputStream = ftpsClient.fetchFile(addFileSeparatorIfNeeded(directory) + filename);
            LOGGER.info("Parsing CSV file {}", filename);
            importCsv(new InputStreamReader(inputStream), filename);
            LOGGER.info("Finished parsing CSV file {}", filename);
            if (date.isAfter(lastUpdated)) {
              lastUpdated = date;
            }
          }
        } catch (IllegalArgumentException e) {
          LOGGER.error("Could not parse date: " + e.getMessage(), e);
        } catch (FtpException e) {
          LOGGER.error("Could not fetch file " + filename + ": " + e.getMessage(), e);
        } catch (CsvImportException e) {
          LOGGER.error("Could not import CSV " + filename + ": " + e.getMessage(), e);
        }
      }
    }

    config = configService.getConfig();
    config.setLastCsvUpdate(lastUpdated.toString(dateTimeFormatter));
    configService.updateConfig(config);
    LOGGER.info("Finished fetching CSV files from {}", hostname);
    ftpsClient.disconnect();
  }

  private void importCsv(final Reader reader, String filename) {
    Long entityId = getEntityIdByEntityClassName(Subject.class.getName());
    csvImportExportService.importCsv(entityId, reader, filename, subjectCsvImportCustomizer);
  }

  private Long getEntityIdByEntityClassName(String entityName) {
    EntityDto entity = entityService.getEntityByClassName(entityName);
    if (entity == null) {
      throw new CsvImportException("Can not find entity named: " + entityName);
    }
    return entity.getId();
  }

  private String addFileSeparatorIfNeeded(String directory) {
    if (!directory.endsWith(TujiokoweConstants.FTP_FILE_SEPARATOR)) {
      return (directory + TujiokoweConstants.FTP_FILE_SEPARATOR);
    }
    return directory;
  }

  private LocalDate getLastCsvUpdate(String lastCsvUpdate, DateTimeFormatter dateTimeFormatter, LocalDate startDate) {
    if (startDate != null) {
      return startDate;
    } else if (StringUtils.isNotBlank(lastCsvUpdate)) {
      return dateTimeFormatter.parseLocalDate(lastCsvUpdate);
    } else {
      return new LocalDate(new Date(0));
    }
  }
}
