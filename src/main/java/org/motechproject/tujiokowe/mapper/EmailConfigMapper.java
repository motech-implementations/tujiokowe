package org.motechproject.tujiokowe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.motechproject.tujiokowe.domain.Config;
import org.motechproject.tujiokowe.dto.EmailReportConfigDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmailConfigMapper {

  EmailConfigMapper INSTANCE = Mappers.getMapper(EmailConfigMapper.class);

  EmailReportConfigDto toDto(Config config);

  void updateFromDto(EmailReportConfigDto emailReportConfigDto, @MappingTarget Config config);
}
