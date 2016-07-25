(function (angular) {
    'use strict';

    /**
     *
     * @param $http
     * @returns {{getAnnouncements: getAnnouncements}}
     * @constructor
     */
    function AnnouncementsService($http) {
        function getAnnouncements(url) {
            return $http.get(url, {
                headers: {
                    Accept: 'text/html'
                }
            }).then(function (data) {
                return data.data;
            })
        }

        function markAnnouncementRead(url) {
            return $http.post(url, {
                headers: {
                    Accept: 'text/html'
                }
            }).then(function (data) {
                return data.data;
            });
        }

        return {
            getAnnouncements: getAnnouncements,
            markAnnouncementRead: markAnnouncementRead
        };
    }

    /**
     *
     * @param svc
     * @param $compile
     * @returns {{template: string, scope: {url: string}, replace: boolean, controllerAs: string, controller: controller}}
     */
    function announcements(svc, $compile) {
        return {
            template: "<div></div>",
            scope: {
                url: '@'
            },
            replace: true,
            controllerAs: 'announcementCtrl',
            controller: function ($scope, $element) {
                function getAnnouncements() {
                    svc.getAnnouncements($scope.url).then(function(data) {
                        $element.html($compile(data)($scope));
                    });
                }
                getAnnouncements();
            }
        }
    }

    angular.module('irida.announcements', [])
        .service('AnnouncementsService', ['$http', AnnouncementsService])
        .directive('announcements', ['AnnouncementsService', '$compile', announcements])
        .controller('AnnouncementItemCtrl', ['$window', '$scope', 'AnnouncementsService',
            function($window, $scope, AnnouncementsService) {
                $scope.markRead = function markRead(aID) {
                    var url = "/announcements/read/" + aID;
                    AnnouncementsService.markAnnouncementRead(url);
                    $scope.$apply();
                };
            }
        ]);

})(window.angular);
