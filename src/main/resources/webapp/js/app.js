(function () {
    'use strict';

    /* App Module */
    var tujiokowe = angular.module('tujiokowe', ['tujiokowe.controllers', 'tujiokowe.directives',
          'motech-dashboard', 'data-services', 'ui.directives']), subjectId, callDetailRecordId, smsRecordId, holidayId;

    $.ajax({
        url: '../mds/entities/getEntity/Tujiokowe/Participant',
        success:  function(data) {
            subjectId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/IVR Module/CallDetailRecord',
        success:  function(data) {
            callDetailRecordId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/SMS Module/SmsRecord',
        success:  function(data) {
            smsRecordId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/Tujiokowe/Holiday',
        success:  function(data) {
            holidayId = data.id;
        },
        async: false
    });

    $.ajax({
            url: '../tujiokowe/available/tujiokoweTabs',
            success:  function(data) {
                tujiokowe.constant('TUJIOKOWE_AVAILABLE_TABS', data);
            },
            async:    false
        });

    tujiokowe.run(function ($rootScope, TUJIOKOWE_AVAILABLE_TABS) {
            $rootScope.TUJIOKOWE_AVAILABLE_TABS = TUJIOKOWE_AVAILABLE_TABS;
        });

    tujiokowe.config(function ($routeProvider, TUJIOKOWE_AVAILABLE_TABS) {
        var i, tab;

        for (i = 0; i < TUJIOKOWE_AVAILABLE_TABS.length; i = i + 1) {

            tab = TUJIOKOWE_AVAILABLE_TABS[i];

            if (tab === "subjects") {
                $routeProvider.when('/tujiokowe/{0}'.format(tab), {
                    templateUrl: '../tujiokowe/resources/partials/tujiokoweInstances.html',
                    controller: 'MdsDataBrowserCtrl',
                    resolve: {
                        entityId: function ($route) {
                            $route.current.params.entityId = subjectId;
                        },
                        moduleName: function ($route) {
                            $route.current.params.moduleName = 'tujiokowe';
                        }
                    }
                });
            } else if (tab === "holidays") {
                $routeProvider.when('/tujiokowe/{0}'.format(tab), {
                    templateUrl: '../tujiokowe/resources/partials/tujiokoweInstances.html',
                    controller: 'MdsDataBrowserCtrl',
                    resolve: {
                        entityId: function ($route) {
                            $route.current.params.entityId = holidayId;
                        },
                        moduleName: function ($route) {
                            $route.current.params.moduleName = 'tujiokowe';
                        }
                    }
                });
            } else if (tab === "reports") {
                $routeProvider
                  .when('/tujiokowe/reports', { templateUrl: '../tujiokowe/resources/partials/reports.html' })
                  .when('/tujiokowe/reports/:reportType', { templateUrl: '../tujiokowe/resources/partials/report.html', controller: 'TujiokoweReportsCtrl' })
                  .when('/tujiokowe/callDetailRecord', { redirectTo: '/mds/dataBrowser/' + callDetailRecordId + '/tujiokowe' })
                  .when('/tujiokowe/SMSLog', { redirectTo: '/mds/dataBrowser/' + smsRecordId + '/tujiokowe' });
            } else if (tab === "enrollment") {
                $routeProvider
                  .when('/tujiokowe/enrollment', {templateUrl: '../tujiokowe/resources/partials/enrollment.html', controller: 'TujiokoweEnrollmentCtrl'})
                  .when('/tujiokowe/enrollmentAdvanced/:subjectId', {templateUrl: '../tujiokowe/resources/partials/enrollmentAdvanced.html', controller: 'TujiokoweEnrollmentAdvancedCtrl'});
            } else {
                $routeProvider.when('/tujiokowe/{0}'.format(tab),
                    {
                        templateUrl: '../tujiokowe/resources/partials/{0}.html'.format(tab),
                        controller: 'Tujiokowe{0}Ctrl'.format(tab.capitalize())
                    }
                );
            }
        }

        $routeProvider
            .when('/tujiokowe/settings', {templateUrl: '../tujiokowe/resources/partials/settings.html', controller: 'TujiokoweSettingsCtrl'})
            .when('/tujiokowe/welcomeTab', { redirectTo: '/tujiokowe/' + TUJIOKOWE_AVAILABLE_TABS[0] });

    });
}());
