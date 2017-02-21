(function (angular, $, tl) {
    angular
        .module('irida.session', [])
        .run(['SessionFactory', runSessionFactory])
        .factory('SessionFactory', ['$timeout', '$interval', '$window', '$uibModal', SessionFactory])
        .factory('HttpInterceptor', ['$injector', HttpInterceptor])
        .filter('countdown', countdownFilter)
    ;

    function countdownFilter () {
        return function(timeleft) {
            if($.isNumeric(timeleft)){
                return moment.duration(timeleft, "milliseconds").humanize();
            }
        };
    }

    function runSessionFactory(session) {
        session.start();
    }

    function SessionFactory($timeout, $interval, $window, $uibModal) {
        var modalWait = 300000, // 5 minutes
            initialWait = tl.SESSION_LENGTH * 1000 - modalWait + 500, // Give a little overlap
            timeout,
            restartTimeout,
            opened = false;

        function _restart() {
            if (!opened) {
                opened = true;
                $uibModal.open({
                    templateUrl: '/session-modal.html',
                    controller : ['$scope', '$http', '$window', '$uibModalInstance', function ($scope, $http, $window, $uibModalInstance) {
                        $scope.poke = function () {
                            $interval.cancel(countdown);
                            $timeout.cancel(restartTimeout);
                            $timeout.cancel(timeout);
                            $http.head($window.location.href).success(function () {
                                start();
                                opened = false;
                                $uibModalInstance.close();
                            });
                        };

                        $scope.logout = function () {
                            $window.location = tl.BASE_URL + "logout";
                        };

                        $scope.timeleft = modalWait;
                        var countdown = $interval(function () {
                            $scope.timeleft = $scope.timeleft - 1000;
                        }, 1000);

                        restartTimeout = $timeout(function () {
                            $window.location.reload();
                        }, modalWait);
                    }]
                });
            }
        }

        function start() {
            timeout = $timeout(function SessionTimer() {
                _restart();
            }, initialWait);
        }

        function reset() {
            $timeout.cancel(timeout);
            start();
        }

        return ({
            start: start,
            reset: reset
        })
    }

    function HttpInterceptor($injector) {
        return {
            request : function (config) {
                if (config.url.indexOf("/template") > -1) {
                    config.headers.Accept = "text/html";
                }
                return config;
            },
            response: function (response) {
                var session = $injector.get('SessionFactory');
                session.reset();
                return response;
            }
        };
    }
})(window.angular, window.jQuery, window.TL);