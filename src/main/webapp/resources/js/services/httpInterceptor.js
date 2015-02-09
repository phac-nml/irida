(function (angular) {
    angular
        .module('irida.session', ['irida.utilities'])
        .run(['SessionFactory', runSessionFactory])
        .config(['$httpProvider', httpProviderConfig])
        .factory('SessionFactory', ['$timeout', '$interval', '$window', '$modal', SessionFactory])
        .factory('HttpInterceptor', ['$injector', HttpInterceptor])
    ;

    function runSessionFactory(session) {
        session.start();
    }

    function httpProviderConfig ($httpProvider) {
        $httpProvider.interceptors.push('HttpInterceptor');

        // Make sure that all ajax form data is sent in the correct format.
        $httpProvider.defaults.transformRequest = function (data) {
            if (data === undefined) {
                return data;
            }
            return $.param(data);
        };
    }

    function SessionFactory($timeout, $interval, $window, $modal) {
        var modalWait = 120000, // 2 minutes
            initialWait = TL.SESSION_LENGTH * 1000 - modalWait + 500, // Give a little overlap
            timeout,
            restartTimeout,
            opened = false;

        function _restart() {
            if (!opened) {
                opened = true;
                $modal.open({
                    templateUrl: '/session-modal.html',
                    controller : ['$scope', '$http', '$window', '$modalInstance', function ($scope, $http, $window, $modalInstance) {
                        $scope.poke = function () {
                            $interval.cancel(countdown);
                            $timeout.cancel(restartTimeout);
                            $timeout.cancel(timeout);
                            $http.head($window.location.href).success(function () {
                                start();
                                opened = false;
                                $modalInstance.close();
                            });
                        };

                        $scope.logout = function () {
                            $window.location = TL.BASE_URL + "logout";
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
})(angular)