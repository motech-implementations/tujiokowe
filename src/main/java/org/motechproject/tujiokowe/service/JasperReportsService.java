package org.motechproject.tujiokowe.service;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.motechproject.tujiokowe.dto.EmailReportConfigDto;

public interface JasperReportsService {

  void sendVaccinationSummaryReport();

  void fetchVaccinationSummaryReport(HttpServletResponse response)
      throws SQLException, JRException, IOException;

  EmailReportConfigDto getEmailReportConfig();

  EmailReportConfigDto saveEmailReportConfig(EmailReportConfigDto emailReportConfigDto);
}
