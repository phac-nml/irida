(function (angular, $, _) {
  function setRootVariable($rootScope) {
    $rootScope.cgPromise = null;
  }

  function Select2Service($timeout) {
    'use strict';
    var svc = this;
    svc.init = function (id, opts) {
      opts = opts || {};
      var timer = $timeout(function () {
        var s = $(id);
        if (s.length > 0) {
          s.select2(opts);
          $timeout.cancel(timer);
        }
      }, 50);
    };
  }


  function SamplesTableFilter(filter, SamplesService) {
    "use strict";
    return function (samples) {
      var begin = filter.page * filter.count;
      var filtered = samples.slice(begin, begin + filter.count);
      SamplesService.setFilteredSamples(filtered);
      return filtered;
    }
  }

  function FilterFactory() {
    "use strict";
    return {
      page    : 0,
      sortDir : 'desc',
      sortedBy: 'createdDate',
      count   : 10
    }
  }

  /*[- */
// Responsible for all server calls for samples
// @param $rootScope The root scope for the page.
// @param R Restangular
  /* -]*/
  function SamplesService($rootScope, R, notifications) {
    "use strict";
    var svc = this,
        id = $rootScope.projectId,
        base = R.all('projects/' + id + '/ajax/samples'),
        selected = [],
        filtered = [];
    svc.samples = [];

    svc.getSamples = function (f) {
      _.extend(svc.filter, f || {});
      $rootScope.cgPromise = base.customGET("").then(function (data) {
        angular.copy(data.samples, svc.samples);
        $rootScope.$broadcast('PAGING_UPDATE', {total: data.samples.length});
      });
    };

    svc.setFilteredSamples = function (f) {
      filtered = f;
    };

    svc.updateSample = function (s) {
      if (s.selected) {
        selected.push(s)
      }
      else {
        selected = _.without(selected, s);
      }
      updateSelectedCount()
    };

    svc.getSelectedSampleNames = function () {
      return selected;
    };

    svc.merge = function (params) {
      params.sampleIds = getSelectedSampleIds();
      return base.customPOST(params, 'merge').then(function (data) {
        if (data.result === 'success') {
          svc.getSamples();
          selected = [];
          updateSelectedCount();
          notifications.show({type: data.result, msg: data.message});
        }
      });
    };

    svc.copy = function (projectId) {
      return copyMoveSamples(projectId, false);
    };

    svc.move = function (projectId) {
      return copyMoveSamples(projectId, true);
    };

    svc.selectPage = function () {
      _.each(filtered, function (s) {
        if (!s.selected) {
          s.selected = true;
          selected.push(s);
        }
      });
      updateSelectedCount();
    };

    svc.selectAll = function () {
      _.each(svc.samples, function (s) {
        s.selected = true;
      });
      selected = svc.samples;
      updateSelectedCount();
    };

    svc.selectNone = function () {
      _.each(svc.samples, function (s) {
        s.selected = false
      });
      selected = [];
      updateSelectedCount();
    };

    function getSelectedSampleIds() {
      return _.map(selected, 'id');
    }

    function copyMoveSamples(projectId, move) {

      return base.customPOST({
        sampleIds         : getSelectedSampleIds(),
        newProjectId      : projectId,
        removeFromOriginal: move
      }, "copy").then(function (data) {
        updateSelectedCount(data.count);
        if (data.result === 'success') {
          notifications.show({msg: data.message});
        }
        _.forEach(data.warnings, function (msg) {
          notifications.show({type: 'info', msg: msg});
        });
        if (move) {
          angular.copy(_.filter(svc.samples, function (s) {
            if (_.has(s, 'selected')) {
              return !s.selected;
            }
            return true;
          }), svc.samples);
          selected = 0;
          updateSelectedCount();
        }
      });
    }

    function updateSelectedCount() {
      $rootScope.$broadcast('COUNT', {count: selected.length});
    }
  }

  /*[- */
// Handles everything to do with paging for the Samples Table
// @param $scope Scope the controller is responsible for
// @param SamplesService Server handler for samples.
  /* -]*/
  function PagingCtrl($scope, SamplesTableFilter) {
    "use strict";
    var vm = this;
    vm.count = 10;
    vm.total = 0;
    vm.page = 1;

    vm.update = function () {
      SamplesTableFilter.page = vm.page - 1;
    };

    $scope.$on('PAGING_UPDATE', function (e, args) {
      vm.total = args.total;
    });
  }

  /*[- */
// Responsible for all samples within the table
// @param SamplesService Server handler for samples.
  /* -]*/
  function SamplesTableCtrl(SamplesService, FilterFactory) {
    "use strict";
    var vm = this;
    vm.open = [];
    vm.filter = FilterFactory;

    vm.samples = SamplesService.samples;

    vm.updateSample = function (s) {
      SamplesService.updateSample(s);
    };

    // Initial call to get the samples
    SamplesService.getSamples({});
  }

  function SubNavCtrl($scope, $modal, BASE_URL, SamplesService) {
    "use strict";
    var vm = this;
    vm.count = 0;

    vm.selection = {
      isopen    : false,
      page      : false,
      selectPage: function selectPage() {
        SamplesService.selectPage();
      },
      selectAll : function selectAll() {
        SamplesService.selectAll();
      },
      selectNone: function selectNone() {
        SamplesService.selectNone();
      }
    };

    vm.samplesOptions = {
      open: false
    };

    vm.merge = function () {
      if (vm.count > 1) {
        $modal.open({
          templateUrl: BASE_URL + 'projects/templates/merge',
          controller : 'MergeCtrl as mergeCtrl',
          resolve    : {
            samples: function () {
              return SamplesService.getSelectedSampleNames();
            }
          }
        });
      }
    };

    vm.openModal = function (type) {
      $modal.open({
        templateUrl: BASE_URL + 'projects/templates/' + type,
        controller : 'CopyMoveCtrl as cmCtrl',
        resolve    : {
          samples: function () {
            return SamplesService.getSelectedSampleNames();
          },
          type   : function () {
            return type;
          }
        }
      });
    };

    $scope.$on('COUNT', function (e, a) {
      vm.count = a.count;
    });
  }

  function MergeCtrl($scope, $modalInstance, Select2Service, SamplesService, samples) {
    "use strict";
    var vm = this;
    vm.samples = samples;
    vm.selected = samples[0];
    vm.name = "";
    vm.error = {};

    Select2Service.init("#samplesSelect");

    vm.close = function () {
      $modalInstance.close();
    };

    vm.merge = function () {
      SamplesService.merge({mergeSampleId: vm.selected.id, newName: vm.name}).then(function () {
        vm.close();
      });
    };

    $scope.$watch(function () {
      return vm.name;
    }, _.debounce(function (n, o) {
      if (n !== o) {
        vm.error.length = n.length < 5 && n.length > 0;
        vm.error.format = n.indexOf(" ") !== -1;
      }
      $scope.$apply();
    }, 300));
  }

  function CopyMoveCtrl($modalInstance, $rootScope, BASE_URL, SamplesService, Select2Service, samples, type) {
    "use strict";
    var vm = this;
    vm.samples = samples;

    vm.close = function () {
      $modalInstance.close();
    };

    vm.go = function () {
      SamplesService[type](vm.selected).then(function () {
        vm.close();
      });
    };

    Select2Service.init("#projectsSelect", {
      minimumLength: 2,
      ajax         : {
        url        : BASE_URL + "projects/ajax/samples/available_projects",
        dataType   : 'json',
        quietMillis: 250,
        data       : function (search, page) {
          return {
            term    : search, // search term
            page    : page - 1, //zero based method
            pageSize: 10
          };
        },
        results    : function (data, page) {
          var results = [];

          var more = (page * 10) < data.total;

          _.forEach(data.projects, function (p) {
            if ($rootScope.projectId !== parseInt(p.id)) {
              results.push({
                id  : p.id,
                text: p.text || p.name
              });
            }
          });

          return {results: results, more: more};
        }
      }
    });
  }

  function SelectedCountCtrl($scope) {
    "use strict";
    var vm = this;
    vm.count = 0;

    $scope.$on('COUNT', function (e, a) {
      vm.count = a.count;
    });
  }

  angular.module('Samples', ['ui.select', 'cgBusy'])
    .run(['$rootScope', setRootVariable])
    .factory('FilterFactory', [FilterFactory])
    .service('Select2Service', ['$timeout', Select2Service])
    .service('SamplesService', ['$rootScope', 'Restangular', 'notifications', SamplesService])
    .filter('SamplesTableFilter', ['FilterFactory', 'SamplesService', SamplesTableFilter])
    .controller('SubNavCtrl', ['$scope', '$modal', 'BASE_URL', 'SamplesService', SubNavCtrl])
    .controller('PagingCtrl', ['$scope', 'FilterFactory', PagingCtrl])
    .controller('SamplesTableCtrl', ['SamplesService', 'FilterFactory', SamplesTableCtrl])
    .controller('MergeCtrl', ['$scope', '$modalInstance', 'Select2Service', 'SamplesService', 'samples', MergeCtrl])
    .controller('CopyMoveCtrl', ['$modalInstance', '$rootScope', 'BASE_URL', 'SamplesService', 'Select2Service', 'samples', 'type', CopyMoveCtrl])
    .controller('SelectedCountCtrl', ['$scope', SelectedCountCtrl]);
})(angular, $, _);
