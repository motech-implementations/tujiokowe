package org.motechproject.tujiokowe.repository;

import java.util.List;
import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.tujiokowe.domain.Subject;
import org.motechproject.tujiokowe.domain.enums.VisitType;

/**
 * Interface for repository that persists simple records and allows CRUD. MotechDataService base
 * class will provide the implementation of this class as well as methods for adding, deleting,
 * saving and finding all instances.  In this class we define and custom lookups we may need.
 */
public interface SubjectDataService extends MotechDataService<Subject> {

  @Lookup(name = "Find unique By Participant Id")
  Subject findBySubjectId(@LookupField(name = "subjectId") String subjectId);

  @Lookup(name = "Find By New Booster Planned Date")
  List<Subject> findByBoostPlannedDateRange(@LookupField(name = "boostPlannedDate")
      Range<LocalDate> dateRange);

  @Lookup(name = "Find By New Booster Planned Date and Slot")
  List<Subject> findByBoostPlannedDateRangeAndSlot(
      @LookupField(name = "boostPlannedDate") Range<LocalDate> dateRange,
      @LookupField(name = "slot") String slot);

  @Lookup(name = "Find By Actual Prime Date Range")
  List<Subject> findByPrimeVaccinationDateRange(@LookupField(name = "primeVaccinationDate")
      Range<LocalDate> dateRange);

  @Lookup(name = "Find By Actual Booster Date Range")
  List<Subject> findByBoostVaccinationDateRange(@LookupField(name = "boostVaccinationDate")
      Range<LocalDate> dateRange);

  @Lookup(name = "Find By Actual Prime Date")
  List<Subject> findByPrimeVaccinationDate(
      @LookupField(name = "primeVaccinationDate") LocalDate dateRange);

  @Lookup(name = "Find By Actual Booster Date")
  List<Subject> findByBoostVaccinationDate(
      @LookupField(name = "boostVaccinationDate") LocalDate dateRange);

  @Lookup(name = "Find By Participant Id")
  List<Subject> findByMatchesCaseInsensitiveSubjectId(@LookupField(name = "subjectId",
      customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

  @Lookup(name = "Find By Site Id")
  List<Subject> findBySiteId(@LookupField(name = "siteId",
      customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteId);

  @Lookup(name = "Find By exact Phone Number")
  List<Subject> findByPhoneNumber(@LookupField(name = "phoneNumber") String phoneNumber);

  @Lookup(name = "Find By Visit Type and Actual Date")
  List<Subject> findByVisitTypeAndActualDate(
      @LookupField(name = "visits.type") VisitType visitType,
      @LookupField(name = "visits.date", customOperator = Constants.Operators.NEQ) LocalDate date);

  @Lookup(name = "Find by Visit Type and Actual Date Range")
  List<Subject> findSubjectByVisitTypeAndActualDateRange(
      @LookupField(name = "visits.type") VisitType visitType,
      @LookupField(name = "visits.date") Range<LocalDate> date);
}
