(function(angular, page){
    function setRootVariable($rootScope) {
        $rootScope.cgPromise = null;
    }

    function RunsService($rootScope, $http, filter) {
        var svc = this;
        svc.runs = [];

        svc.getRuns = function() {
            $rootScope.cgPromise = $http.get(page.urls.list).success(function(data) {
                angular.copy(data, svc.runs);
                $rootScope.$broadcast('RUNS_INIT', {
                    total : data.length
                });
            });
            return svc.runs;
        }

    }

    function RunsTableCtrl(filter, RunsService) {
        "use strict";
        var vm = this;
        vm.filter = filter;
        vm.runs = RunsService.getRuns();

    }

    function PagingFilter($rootScope, filter, RunsService) {
        "use strict";
        return function(runs) {
            $rootScope.$broadcast('PAGING_UPDATE', {
                total : runs.length
            });
            var begin = filter.page * filter.count;
            return runs.slice(begin, begin + filter.count);
        }
    }

    function SortCtrl($rootScope, filter) {
        "use strict";
        var vm = this;
        vm.filter = filter;

        vm.onSort = function(sortedBy, sortDir) {
            vm.filter.sortedBy = sortedBy;
            vm.filter.sortDir = sortDir;
            $rootScope.$broadcast('PAGE_CHANGE', {
                page : 1
            });
        }
    }

    function FilterFactory() {
        "use strict";
        return {
            page : 0,
            sortDir : true,
            sortedBy : 'createdDate',
            count : 10
        }
    }

    function PagingCtrl($scope, filter) {
        "use strict";
        var vm = this;
        vm.count = 10;
        vm.total = 0;
        vm.page = 1;

        vm.update = function() {
            filter.page = vm.page - 1;
        };

        $scope.$on('PAGING_UPDATE', function(e, args) {
            vm.total = args.total;
        });

        $scope.$on('PAGE_CHANGE', function(e, args) {
            vm.page = args.page;
            vm.update();
        });
    }

    function sortBy() {
        'use strict';
        return {
            template : '<a class="clickable" ng-click="sort(sortValue)">'
            + '<span ng-transclude=""></span>'
            + '<span class="pull-right" ng-show="sortedby == sortvalue">'
            + '<i class="fa fa-fw" ng-class="{true: \'fa-sort-asc\', false: \'fa-sort-desc\'}[sortdir]"></i>'
            + '</span><span class="pull-right" ng-show="sortedby != sortvalue"><i class="fa fa-sort fa-fw"></i></span></a>',
            restrict : 'EA',
            transclude : true,
            replace : false,
            scope : {
                sortdir : '=',
                sortedby : '=',
                sortvalue : '@',
                onsort : '='
            },
            link : function(scope) {
                scope.sort = function() {
                    if (scope.sortedby === scope.sortvalue) {
                        scope.sortdir = !scope.sortdir;
                    } else {
                        scope.sortedby = scope.sortvalue;
                        scope.sortdir = true;
                    }
                    scope.onsort(scope.sortedby, scope.sortdir);
                };
            }
        };
    }

    angular.module('SequencingRuns', [ 'cgBusy' ])
        .run([ '$rootScope', setRootVariable ])
        .factory('FilterFactory', [ FilterFactory ])
        .service('RunsService', [ '$rootScope', '$http', 'FilterFactory', RunsService ])
        .filter('PagingFilter', [ '$rootScope', 'FilterFactory', 'RunsService', PagingFilter ])
        .directive('sortBy', [ sortBy ]).controller('PagingCtrl', [ '$scope', 'FilterFactory', PagingCtrl ])
        .controller('SortCtrl', [ '$rootScope', 'FilterFactory', SortCtrl ])
        .controller('RunsTableCtrl',	[ 'FilterFactory', 'RunsService', RunsTableCtrl ]);
})(window.angular, window.PAGE);