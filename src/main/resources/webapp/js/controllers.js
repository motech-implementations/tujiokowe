(function() {
    'use strict';

    var controllers = angular.module('tujiokowe.controllers', []);

    controllers.controller('TujiokoweBaseCtrl', function ($scope, $timeout, $http, MDSUtils) {

        $scope.filters = [{
            name: $scope.msg('tujiokowe.today'),
            dateFilter: "TODAY"
        },{
            name: $scope.msg('tujiokowe.tomorrow'),
            dateFilter: "TOMORROW"
        },{
            name: $scope.msg('tujiokowe.twoDaysAfter'),
            dateFilter: "TWO_DAYS_AFTER"
        },{
            name: $scope.msg('tujiokowe.nextThreeDays'),
            dateFilter: "NEXT_THREE_DAYS"
        },{
            name: $scope.msg('tujiokowe.thisWeek'),
            dateFilter: "THIS_WEEK"
        },{
            name: $scope.msg('tujiokowe.dateRange'),
            dateFilter: "DATE_RANGE"
        }];

        $scope.selectedFilter = $scope.filters[0];

        $scope.selectFilter = function(value) {
            $scope.selectedFilter = $scope.filters[value];
            if (value !== 5) {
                $scope.refreshGrid();
            }
        };

        $scope.cardDateFormat = "dd-MM-yyyy";
        $scope.cardDateTimeFormat = "dd-MM-yyyy HH:mm";

        $scope.availableExportRecords = ['All','10', '25', '50', '100', '250'];
        $scope.availableExportFormats = ['pdf', 'csv'];
        $scope.actualExportRecords = 'All';
        $scope.actualExportColumns = 'All';
        $scope.exportFormat = 'pdf';
        $scope.checkboxModel = {
            exportWithOrder : false,
            exportWithLookup : true
        };
        $scope.disableExportButton = false;
        $scope.exportTaskId = null;
        $scope.exportProgress = 0;
        $scope.exportStatusTimer = null;
        $scope.exportData = [];

        $scope.exportFileName = 'Instance';

        $scope.showFieldSelect = false;

        $scope.exportEntityInstances = function () {
            $scope.checkboxModel.exportWithLookup = true;
            $('#exportTujiokoweInstanceModal').modal('show');
        };

        $scope.changeExportRecords = function (records) {
            $scope.actualExportRecords = records;
        };

        $scope.changeExportFormat = function (format) {
            $scope.exportFormat = format;
        };

        $scope.closeExportTujiokoweInstanceModal = function () {
            $scope.cancelExport();

            $('#exportTujiokoweInstanceForm').resetForm();
            $('#exportTujiokoweInstanceModal').modal('hide');
        };

        $scope.saveFile = function (data, name, type) {
            var filename = name + "_" + new Date().toISOString().replace(/[T\-:]/g, '').substring(0, 14) + "." + type;
            var fileType;

            switch (type) {
                case "pdf":
                    fileType = "application/pdf";
                    break;
                case "xls":
                    fileType = "application/vnd.ms-excel";
                    break;
                default:
                    fileType = "text/csv";
            }

            var file = new Blob(data, {type: type});

            if (window.navigator.msSaveOrOpenBlob) // IE10+
                window.navigator.msSaveOrOpenBlob(file, filename);
            else { // Others
                var a = document.createElement("a"),
                  url = URL.createObjectURL(file);
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                setTimeout(function() {
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                }, 0);
            }
        };

        $scope.finishExport = function() {
            $scope.disableExportButton = false;
            $scope.exportProgress = 0;
            $scope.exportData = [];
            $scope.exportTaskId = null;

            if ($scope.exportStatusTimer) {
                clearInterval($scope.exportStatusTimer);
                $scope.exportStatusTimer = null;
            }

            $scope.closeExportTujiokoweInstanceModal();
        };

        $scope.cancelExport = function() {
            if ($scope.exportTaskId) {
                $http.get("../tujiokowe/export/" + $scope.exportTaskId + "/cancel");
                $scope.finishExport();
            }
        };

        $scope.checkExportStatus = function() {
            $http.get("../tujiokowe/export/" + $scope.exportTaskId + "/status")
              .success(function (data) {
                  $scope.exportProgress = Math.floor(data.progress * 100);

                  if (data.data && data.data.length > 0) {
                      var byteString = atob(data.data);
                      var ab = new ArrayBuffer(byteString.length);
                      var ia = new Uint8Array(ab);

                      for (var i = 0; i < byteString.length; i++) {
                          ia[i] = byteString.charCodeAt(i);
                      }

                      $scope.exportData.push(ab);
                  }

                  if (data.status === 'FAILED' || data.status === 'CANCELED') {
                      $scope.finishExport();
                      motechAlert('mds.error', 'mds.error.exportData');
                  } else if (data.status === 'FINISHED') {
                      $scope.saveFile($scope.exportData, $scope.exportFileName, $scope.exportFormat);
                      $scope.finishExport();
                  }
              })
              .error(function (response) {
                  $scope.finishExport();
                  handleResponse('mds.error', 'mds.error.exportData', response);
              });
        };

        $scope.exportInstanceWithUrl = function(url, fileName) {
            $scope.disableExportButton = true;
            $scope.exportProgress = 0;
            $scope.exportData = [];
            $scope.exportFileName = fileName;

            if ($scope.selectedLookup !== undefined && $scope.checkboxModel.exportWithLookup === true) {
                url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
                url = url + "&fields=" + encodeURIComponent(JSON.stringify($scope.lookupBy));
            }

            $http.get(url)
            .success(function (data) {
                $scope.exportTaskId = data;

                setTimeout(function(){$scope.checkExportStatus()}, 1500);
                $scope.exportStatusTimer = setInterval(function(){$scope.checkExportStatus()}, 5000);
            })
            .error(function (response) {
                $scope.finishExport();
                handleResponse('mds.error', 'mds.error.exportData', response);
            });
        };

        $scope.parseDate = function(date, offset) {
            if (date !== undefined && date !== null) {
                var parts = date.split('-'), date;

                if (offset) {
                    date = new Date(parts[0], parts[1] - 1, parseInt(parts[2]) + offset);
                } else {
                    date = new Date(parts[0], parts[1] - 1, parts[2]);
                }
                return date;
            }
            return undefined;
        };

        $scope.lookupBy = {};
        $scope.selectedLookup = undefined;
        $scope.lookupFields = [];

        $scope.getLookups = function(url) {
            $scope.lookupBy = {};
            $scope.selectedLookup = undefined;
            $scope.lookupFields = [];

            $http.get(url)
            .success(function(data) {
                $scope.lookups = data;
            });
        };

        /**
        * Shows/Hides lookup dialog
        */
        $scope.showLookupDialog = function() {
            $("#lookup-dialog")
            .css({'top': ($("#lookupDialogButton").offset().top - $("#main-content").offset().top) - 40,
            'left': ($("#lookupDialogButton").offset().left - $("#main-content").offset().left) - 15})
            .toggle();
            $("div.arrow").css({'left': 50});
        };

        $scope.hideLookupDialog = function() {
            $("#lookup-dialog").hide();
        };

        /**
        * Marks passed lookup as selected. Sets fields that belong to the given lookup and resets lookupBy object
        * used to filter instances by given values
        */
        $scope.selectLookup = function(lookup) {
            $scope.selectedLookup = lookup;
            $scope.lookupFields = lookup.lookupFields;
            $scope.lookupBy = {};
        };

        /**
        * Removes lookup and resets all fields associated with a lookup
        */
        $scope.removeLookup = function() {
            $scope.lookupBy = {};
            $scope.selectedLookup = undefined;
            $scope.lookupFields = [];
            $scope.filterInstancesByLookup();
        };

        /**
        * Hides lookup dialog and sends signal to refresh the grid with new data
        */
        $scope.filterInstancesByLookup = function() {
            $scope.showLookupDialog();
            $scope.refreshGrid();
        };

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.buildLookupFieldName = function (field) {
            if (field.relatedName !== undefined && field.relatedName !== '' && field.relatedName !== null) {
                return field.name + "." + field.relatedName;
            }
            return field.name;
        };

        /**
        * Depending on the field type, includes proper html file containing visual representation for
        * the object type. Radio input for boolean, select input for list and text input as default one.
        */
        $scope.loadInputForLookupField = function(field) {
            var value = "default", type = "field";

            if (field.className === "java.lang.Boolean") {
                value = "boolean";
            } else if (field.className === "java.util.Collection") {
                value = "list";
            } else if (field.className === "org.joda.time.DateTime" || field.className === "java.util.Date") {
                value = "datetime";
            } else if (field.className === "org.joda.time.LocalDate") {
                value = "date";
            }

            if ($scope.isRangedLookup(field)) {
                type = "range";
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = {min: '', max: ''};
                }
            } else if ($scope.isSetLookup(field)) {
                type = 'set';
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = [];
                }
            }

            return '../tujiokowe/resources/partials/lookups/{0}-{1}.html'.format(type, value);
        };

        $scope.isRangedLookup = function(field) {
            return $scope.isLookupFieldOfType(field, 'RANGE');
        };

        $scope.isSetLookup = function(field) {
            return $scope.isLookupFieldOfType(field, 'SET');
        };

        $scope.isLookupFieldOfType = function(field, type) {
            var i, lookupField;
            for (i = 0; i < $scope.selectedLookup.lookupFields.length; i += 1) {
                lookupField = $scope.selectedLookup.lookupFields[i];
                if ($scope.buildLookupFieldName(lookupField) === $scope.buildLookupFieldName(field)) {
                    return lookupField.type === type;
                }
            }
        };

        $scope.getComboboxValues = function (settings) {
            var labelValues = MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value, keys = [], key;
            // Check the user supplied flag, if true return string set
            if (MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.allowUserSupplied'}], true).value === true){
                return labelValues;
            } else {
                if (labelValues !== undefined && labelValues[0].indexOf(":") !== -1) {
                    labelValues =  $scope.getAndSplitComboboxValues(labelValues);
                    for(key in labelValues) {
                        keys.push(key);
                    }
                    return keys;
                } else {        // there is no colon, so we are dealing with a string set, not a map
                    return labelValues;
                }
            }
        };

        $scope.getComboboxDisplayName = function (settings, value) {
            var labelValues = MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value;
            // Check the user supplied flag, if true return string set
            if (MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.allowUserSupplied'}], true).value === true){
                return value;
            } else {
                if (labelValues[0].indexOf(":") === -1) { // there is no colon, so we are dealing with a string set, not a map
                    return value;
                } else {
                    labelValues =  $scope.getAndSplitComboboxValues(labelValues);
                    return labelValues[value];
                }
            }

        };

        $scope.getAndSplitComboboxValues = function (labelValues) {
            var doublet, i, map = {};
            for (i = 0; i < labelValues.length; i += 1) {
                doublet = labelValues[i].split(":");
                map[doublet[0]] = doublet[1];
            }
            return map;
        };

        $scope.resizeGridHeight = function(gridId) {
            var intervalHeightResize, gap, tableHeight;
            clearInterval(intervalHeightResize);
            intervalHeightResize = setInterval( function () {
                if ($('.overrideJqgridTable').offset() !== undefined) {
                    gap = 1 + $('.overrideJqgridTable').offset().top - $('.inner-center .ui-layout-content').offset().top;
                    tableHeight = Math.floor($('.inner-center .ui-layout-content').height() - gap - $('.ui-jqgrid-pager').outerHeight() - $('.ui-jqgrid-hdiv').outerHeight());
                    $('#' + gridId).jqGrid("setGridHeight", tableHeight);
                }
                clearInterval(intervalHeightResize);
            }, 250);
         };

        $scope.resizeGridWidth = function(gridId) {
            var intervalWidthResize, tableWidth;
            clearInterval(intervalWidthResize);
            intervalWidthResize = setInterval( function () {
                tableWidth = $('.overrideJqgridTable').width();
                $('#' + gridId).jqGrid("setGridWidth", tableWidth);
                clearInterval(intervalWidthResize);
            }, 250);
        }
    });

    controllers.controller('TujiokoweSettingsCtrl', function ($scope, $http, $timeout) {
        $scope.errors = [];
        $scope.messages = [];

        $http.get('../tujiokowe/tujiokowe-config')
            .success(function(response){
                var i;
                $scope.config = response;
                $scope.originalConfig = angular.copy($scope.config);
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('tujiokowe.settings.noConfig', response));
            });

        $scope.reset = function () {
            $scope.config = angular.copy($scope.originalConfig);
        };

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        }

        $scope.submit = function () {
            $http.post('../tujiokowe/tujiokowe-config', $scope.config)
                .success(function (response) {
                    $scope.config = response;
                    $scope.originalConfig = angular.copy($scope.config);
                    var index = $scope.messages.push($scope.msg('tujiokowe.settings.saved'));
                    hideMsgLater(index-1);
                })
                .error (function (response) {
                    //todo: better than that!
                    handleWithStackTrace('tujiokowe.error.header', 'tujiokowe.error.body', response);
                });
        };
    });

    controllers.controller('TujiokoweRescheduleCtrl', function ($scope, $http, $timeout, $filter) {
        $scope.getLookups("../tujiokowe/getLookupsForVisitReschedule");

        $scope.$parent.selectedFilter.startDate = undefined;
        $scope.$parent.selectedFilter.endDate = undefined;
        $scope.$parent.selectedFilter = $scope.filters[5];
        $scope.visitForPrint = {};

        $scope.newForm = function() {
            $scope.form = {};
            $scope.form.dto = {};
        };

        $scope.setActualDateToCurrentDate = function () {
            $scope.form.dto.actualDate = $filter('date')(new Date(), "yyyy-MM-dd");
        };

        $scope.showPlannedDate = function () {
            var isActualDateEmpty = $scope.form.dto.actualDate === null || $scope.form.dto.actualDate === "" || $scope.form.dto.actualDate === undefined;
            return isActualDateEmpty;
        };

        $scope.clearActualDate = function () {
           motechConfirm("tujiokowe.visitReschedule.removeActualDate", "tujiokowe.confirm", function(confirmed) {
                   if (confirmed) {
                       $scope.form.dto.actualDate = null;
                       $timeout(function() {
                           $('#actualDateInput').trigger('change');
                       }, 100);
                   }
           })
        };

        $scope.showRescheduleModal = function(modalHeaderMessage, modalBodyMessage) {
            $timeout(function() {
            $scope.rescheduleModalHeader = modalHeaderMessage;
            $scope.rescheduleModalBody = modalBodyMessage;
            $('#visitRescheduleModal').modal('show');
            $scope.setDatePicker();
            }, 10);
        };

        $scope.getUploadSuccessMessageAndTogglePrintButton = function () {
            if ($scope.form.dto && $scope.form.dto.actualDate) {
                $scope.rescheduleModalBody = $scope.msg('tujiokowe.visitReschedule.actualDateUpdateSuccessful');
                $scope.diablePrint = true;
            } else {
                $scope.rescheduleModalBody = $scope.msg('tujiokowe.visitReschedule.plannedDateUpdateSuccessful');
                $scope.diablePrint = false;
            }
        };

        $scope.setDatePicker = function () {
            var plannedDate = $scope.parseDate($scope.form.dto.plannedDate);
            var minDate = $scope.form.dto.minDate;

            if (plannedDate && minDate && plannedDate < minDate) {
                minDate = plannedDate;
            }

            var plannedDateInput = $('#plannedDateInput');
            plannedDateInput.datepicker("setDate", plannedDate);
            plannedDateInput.datepicker('option', 'minDate', minDate);
            plannedDateInput.datepicker('option', 'maxDate', $scope.form.dto.maxDate);

            var actualDateInput = $('#actualDateInput');
            actualDateInput.datepicker('option', 'minDate', $scope.form.dto.minActualDate);
            actualDateInput.datepicker('option', 'maxDate', $scope.form.dto.maxActualDate);
            if ($scope.form.dto.actualDate) {
                var actualDate = $scope.parseDate($scope.form.dto.actualDate);
                actualDateInput.datepicker("setDate", actualDate);
            } else {
                actualDateInput.datepicker("setDate", null);
            }
        };

        $scope.saveVisitReschedule = function(ignoreLimitation) {
            function sendRequest() {
                $scope.getUploadSuccessMessageAndTogglePrintButton();
                $http.post('../tujiokowe/saveVisitReschedule/' + ignoreLimitation, $scope.form.dto)
                    .success(function(data) {
                        if (data && (typeof(data) === 'string')) {
                            jConfirm($scope.msg('tujiokowe.visitReschedule.confirmMsg', data), $scope.msg('tujiokowe.visitReschedule.confirmTitle'),
                                function (response) {
                                    if (response) {
                                        $scope.saveVisitReschedule(true);
                                    }
                                });
                        } else {
                            $("#visitReschedule").trigger('reloadGrid');
                            $scope.visitForPrint = data;
                            $scope.form.dto = undefined;
                        }
                    })
                    .error(function(response) {
                        motechAlert('tujiokowe.visitReschedule.updateError', 'tujiokowe.error', response);
                    });
            }

            if (ignoreLimitation) {
                sendRequest();
            } else {
                var confirmMsg = "tujiokowe.visitReschedule.confirm.shouldSavePlannedDate";
                if ($scope.form.dto.actualDate !== ""
                    && $scope.form.dto.actualDate !== undefined
                    && $scope.form.dto.actualDate !== null) {
                    confirmMsg = "tujiokowe.visitReschedule.confirm.shouldSaveActualDate";
                }
                motechConfirm(confirmMsg, "tujiokowe.confirm",
                    function(confirmed) {
                        if (confirmed) {
                            var daysBetween = Math.round((new Date - $scope.parseDate($scope.form.dto.actualDate))/(1000*60*60*24));
                            if (daysBetween > 7) {
                                motechConfirm("tujiokowe.visitReschedule.confirm.shouldSaveOldActualDate", "tujiokowe.confirm",
                                    function(confirmed) {
                                    if (confirmed) {
                                        sendRequest();
                                    }
                                })
                            } else {
                                sendRequest();
                            }
                        }
                })
            }
        };

        $scope.formIsFilled = function() {
            return $scope.form
                && $scope.form.dto
                && ($scope.form.dto.actualDate || $scope.form.dto.plannedDate);
        };

        $scope.exportInstance = function() {
            var sortColumn, sortDirection, url = "../tujiokowe/exportInstances/visitReschedule";
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

            if ($scope.checkboxModel.exportWithLookup === true) {
                url = url + "&dateFilter=" + $scope.selectedFilter.dateFilter;

                if ($scope.selectedFilter.startDate) {
                    url = url + "&startDate=" + $scope.selectedFilter.startDate;
                }

                if ($scope.selectedFilter.endDate) {
                    url = url + "&endDate=" + $scope.selectedFilter.endDate;
                }
            }

            if ($scope.checkboxModel.exportWithOrder === true) {
                sortColumn = $('#visitReschedule').getGridParam('sortname');
                sortDirection = $('#visitReschedule').getGridParam('sortorder');

                url = url + "&sortColumn=" + sortColumn;
                url = url + "&sortDirection=" + sortDirection;
            }

            $scope.exportInstanceWithUrl(url, 'VisitReschedule');
        };

        $scope.$watch('form.dto.ignoreDateLimitation', function (value) {
            if ($scope.form && $scope.form.dto) {
                var plannedDate = $scope.parseDate($scope.form.dto.plannedDate);
                var minDate = $scope.earliestDateToReturn;

                if (!value) {
                    $scope.form.dto.maxDate = $scope.latestDateToReturn;
                }

                if (plannedDate && minDate && plannedDate < minDate) {
                    minDate = plannedDate;
                }

                $scope.form.dto.minDate = minDate;
            }
        });

        $scope.setPrintData = function(document, participantId, plannedDate) {

            $('#versionDate', document).html($filter('date')(new Date(), $scope.cardDateTimeFormat));
            $('#subjectId', document).html(participantId);
            $('#date', document).html($filter('date')($scope.parseDate(plannedDate), $scope.cardDateFormat));
        };

        $scope.print = function() {

            setTimeout(function() {
                var subjectId = $scope.visitForPrint.participantId;
                var date = $scope.visitForPrint.plannedDate;

                var winPrint = window.open("../tujiokowe/resources/partials/card/visitRescheduleCard.html");
                 if ((!(window.ActiveXObject) && "ActiveXObject" in window) || (navigator.userAgent.indexOf("MSIE") > -1)) {
                   // iexplorer
                    var windowOnload = winPrint.onload || function() {
                        setTimeout(function(){
                            $scope.setPrintData(winPrint.document, subjectId, date);
                            winPrint.focus();
                            winPrint.print();
                        }, 500);
                      };

                      winPrint.onload = new function() { windowOnload(); } ;
                 } else {
                    winPrint.onload = function() {
                        $scope.setPrintData(winPrint.document, subjectId, date);
                        winPrint.focus();
                        winPrint.print();
                    }
                 }
             }, 500);
        };
    });

    controllers.controller('TujiokoweReportsCtrl', function ($scope, $routeParams) {
        $scope.reportType = $routeParams.reportType;
        $scope.reportName = "";

        $scope.$parent.selectedFilter.startDate = undefined;
        $scope.$parent.selectedFilter.endDate = undefined;
        $scope.$parent.selectedFilter = $scope.filters[0];

        $scope.buildColumnModel = function (colModel) {
            var newColModel = colModel;
            for (var i in colModel) {
                if(!colModel[i].hasOwnProperty('formatoptions') && colModel[i].hasOwnProperty('formatter')) {
                    newColModel[i].formatter = eval("(" + colModel[i].formatter + ")");
                }
            }
            return newColModel;
        };

        $scope.buildColumnNames = function (colNames) {
            var newColNames = colNames;
            for(var i in colNames) {
                newColNames[i] = $scope.msg(colNames[i]);
            }
            return newColNames;
        };

        var url;
        switch($scope.reportType){
            case "dailyClinicVisitScheduleReport":
                url = "../tujiokowe/getLookupsForDailyClinicVisitScheduleReport";
                $scope.reportName = $scope.msg('tujiokowe.reports.dailyClinicVisitScheduleReport');
                break;
            case "followupsMissedClinicVisitsReport":
                url = "../tujiokowe/getLookupsForFollowupsMissedClinicVisitsReport";
                $scope.reportName = $scope.msg('tujiokowe.reports.followupsMissedClinicVisitsReport');
                break;
            case "MandEMissedClinicVisitsReport":
                url = "../tujiokowe/getLookupsForMandEMissedClinicVisitsReport";
                $scope.reportName = $scope.msg('tujiokowe.reports.MandEMissedClinicVisitsReport');
                break;
            case "ivrAndSmsStatisticReport":
                url = "../tujiokowe/getLookupsForIvrAndSmsStatisticReport";
                $scope.reportName = $scope.msg('tujiokowe.reports.ivrAndSmsStatisticReport');
                break;
            case "optsOutOfMotechMessagesReport":
                url = "../tujiokowe/getLookupsForOptsOutOfMotechMessagesReport";
                $scope.reportName = $scope.msg('tujiokowe.reports.optsOutOfMotechMessagesReport');
                break;
        }
        $scope.getLookups(url);

        $scope.exportInstance = function() {
            var url, fileName, sortColumn, sortDirection;

            switch($scope.reportType){
                case "dailyClinicVisitScheduleReport":
                    url = "../tujiokowe/exportDailyClinicVisitScheduleReport";
                    fileName = "DailyClinicVisitScheduleReport";
                    break;
                case "followupsMissedClinicVisitsReport":
                    url = "../tujiokowe/exportFollowupsMissedClinicVisitsReport";
                    fileName = "FollowupsMissedClinicVisitsReport";
                    break;
                case "MandEMissedClinicVisitsReport":
                    url = "../tujiokowe/exportMandEMissedClinicVisitsReport";
                    fileName = "MandEMissedClinicVisitsReport";
                    break;
                case "optsOutOfMotechMessagesReport":
                    url = "../tujiokowe/exportOptsOutOfMotechMessagesReport";
                    fileName = "ParticipantsWhoOptOutOfReceivingMotechMessagesReport";
                    break;
                case "ivrAndSmsStatisticReport":
                    url = "../tujiokowe/exportIvrAndSmsStatisticReport";
                    fileName = "NumberOfTimesParticipantsListenedToEachMessageReport";
                    break;
            }
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

            if ($scope.checkboxModel.exportWithOrder === true) {
                sortColumn = $('#reportTable').getGridParam('sortname');
                sortDirection = $('#reportTable').getGridParam('sortorder');

                url = url + "&sortColumn=" + sortColumn;
                url = url + "&sortDirection=" + sortDirection;
            }

            if ($scope.checkboxModel.exportWithLookup === true) {
                url = url + "&dateFilter=" + $scope.selectedFilter.dateFilter;

                if ($scope.selectedFilter.startDate) {
                    url = url + "&startDate=" + $scope.selectedFilter.startDate;
                }

                if ($scope.selectedFilter.endDate) {
                    url = url + "&endDate=" + $scope.selectedFilter.endDate;
                }
            }

            $scope.exportInstanceWithUrl(url, fileName);
        };

        $scope.backToEntityList = function() {
            window.location.replace('#/tujiokowe/reports');
        };
    });

    controllers.controller('TujiokoweEnrollmentCtrl', function ($scope, $http) {
        var url = "../tujiokowe/getLookupsForEnrollments";
        $scope.getLookups(url);

        $scope.enrollInProgress = false;

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.refreshGridAndStayOnSamePage = function() {
            $scope.gridRefresh = !$scope.gridRefresh;
        };

        $scope.enroll = function(subjectId) {
            motechConfirm("tujiokowe.enrollSubject.ConfirmMsg", "tujiokowe.enrollSubject.ConfirmTitle",
              function (response) {
                  if (!response) {
                      return;
                  } else {
                      $scope.enrollInProgress = true;
                      $http.post('../tujiokowe/enrollSubject', subjectId)
                        .success(function(response) {
                            motechAlert('tujiokowe.enrollment.enrollSubject.success', 'tujiokowe.enrollment.enrolledSubject');
                            $scope.refreshGridAndStayOnSamePage();
                            $scope.enrollInProgress = false;
                        })
                        .error(function(response) {
                            motechAlert('tujiokowe.enrollment.enrollSubject.error', 'tujiokowe.enrollment.error', response);
                            $scope.refreshGridAndStayOnSamePage();
                            $scope.enrollInProgress = false;
                        });
                  }
              });
        };

        $scope.unenroll = function(subjectId) {
            motechConfirm("tujiokowe.unenrollSubject.ConfirmMsg", "tujiokowe.unenrollSubject.ConfirmTitle",
              function (response) {
                  if (!response) {
                      return;
                  } else {
                      $scope.enrollInProgress = true;
                      $http.post('../tujiokowe/unenrollSubject', subjectId)
                        .success(function(response) {
                            motechAlert('tujiokowe.enrollment.unenrollSubject.success', 'tujiokowe.enrollment.unenrolledSubject');
                            $scope.refreshGridAndStayOnSamePage();
                            $scope.enrollInProgress = false;
                        })
                        .error(function(response) {
                            motechAlert('tujiokowe.enrollment.unenrollSubject.error', 'tujiokowe.enrollment.error', response);
                            $scope.refreshGridAndStayOnSamePage();
                            $scope.enrollInProgress = false;
                        });
                  }
              });
        };

        $scope.goToAdvanced = function(subjectId) {
            $.ajax({
                url: '../tujiokowe/checkAdvancedPermissions',
                success:  function(data) {
                    window.location.replace('#/tujiokowe/enrollmentAdvanced/' + subjectId);
                },
                async: false
            });
        };

        $scope.exportInstance = function() {
            var url, sortColumn, sortDirection;

            url = "../tujiokowe/exportSubjectEnrollment";
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

            if ($scope.checkboxModel.exportWithOrder === true) {
                sortColumn = $('#enrollmentTable').getGridParam('sortname');
                sortDirection = $('#enrollmentTable').getGridParam('sortorder');

                url = url + "&sortColumn=" + sortColumn;
                url = url + "&sortDirection=" + sortDirection;
            }

            $scope.exportInstanceWithUrl(url, 'ParticipantEnrollments');
        };
    });

    controllers.controller('TujiokoweEnrollmentAdvancedCtrl', function ($scope, $http, $timeout, $routeParams) {
        $scope.enrollInProgress = false;

        $scope.backToEnrolments = function() {
            window.location.replace('#/tujiokowe/enrollment');
        };

        $scope.selectedSubjectId = $routeParams.subjectId;

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.enroll = function(campaignName) {
            $scope.enrollInProgress = true;
            $http.get('../tujiokowe/enrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName)
              .success(function(response) {
                  motechAlert('tujiokowe.enrollment.enrollSubject.success', 'tujiokowe.enrollment.enrolledSubject');
                  $scope.refreshGrid();
                  $scope.enrollInProgress = false;
              })
              .error(function(response) {
                  motechAlert('tujiokowe.enrollment.enrollSubject.error', 'tujiokowe.enrollment.error', response);
                  $scope.refreshGrid();
                  $scope.enrollInProgress = false;
              });
        };

        $scope.unenroll = function(campaignName) {
            $scope.enrollInProgress = true;
            $http.get('../tujiokowe/unenrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName)
              .success(function(response) {
                  motechAlert('tujiokowe.enrollment.unenrollSubject.success', 'tujiokowe.enrollment.unenrolledSubject');
                  $scope.refreshGrid();
                  $scope.enrollInProgress = false;
              })
              .error(function(response) {
                  motechAlert('tujiokowe.enrollment.unenrollSubject.error', 'tujiokowe.enrollment.error', response);
                  $scope.refreshGrid();
                  $scope.enrollInProgress = false;
              });
        };
    });

    /*
     *
     * Email Reports
     *
     */
    controllers.controller('TujiokoweEmailReportsCtrl', function ($scope, $http, $timeout) {

        $scope.schedulePeriods = ['DAILY', 'WEEKLY', 'MONTHLY'];

        $scope.selectPeriod = function(config, value) {
            config.emailSchedulePeriod = $scope.schedulePeriods[value];
        };

        $scope.saveReport = function () {
            $http.get("../tujiokowe/fetchVaccinationSummaryReport", { responseType: 'blob' })
              .success(function (data) {
                  $scope.saveFile([data], 'VaccinationSummaryReport', 'pdf');
              })
              .error(function (response) {
                  handleResponse('mds.error', 'mds.error.exportData', response);
              });
        };

        $scope.errors = [];
        $scope.messages = [];

        $http.get('../tujiokowe/emailReportConfig')
          .success(function(response){
              $scope.config = response;
              $scope.originalConfig = angular.copy($scope.config);
          })
          .error(function(response) {
              $scope.errors.push($scope.msg('tujiokowe.error.header', response));
          });

        $scope.reset = function () {
            $scope.config = angular.copy($scope.originalConfig);
        };

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        }

        $scope.submit = function () {
            $http.post('../tujiokowe/emailReportConfig', $scope.config)
              .success(function (response) {
                  $scope.config = response;
                  $scope.originalConfig = angular.copy($scope.config);
                  var index = $scope.messages.push($scope.msg('tujiokowe.settings.saved'));
                  hideMsgLater(index-1);
              })
              .error (function (response) {
                  //todo: better than that!
                  handleWithStackTrace('tujiokowe.error.header', 'tujiokowe.error.body', response);
              });
        };
    });

}());
