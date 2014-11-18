/* jshint undef: true, unused: true */
/* global angular, $, _ */


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

  /*[- */
// Responsible for all server calls for samples
// @param $rootScope The root scope for the page.
// @param R Restangular
  /* -]*/
  function SamplesService($rootScope, R, notifications) {
    "use strict";
    var svc = this,
        id = $rootScope.projectId,
        _filter = {
          page    : 0,
          sortDir : 'desc',
          sortedBy: 'createdDate',
          count   : 10
        },
        base = R.all('projects/' + id + '/ajax/samples');

    svc.filter = _.clone(_filter);
    svc.samples = [];

    svc.getSamples = function (f) {
      _.extend(svc.filter, f || {});
      $rootScope.cgPromise = base.customGET("", svc.filter).then(function (data) {
        angular.copy(data.samples, svc.samples);
        $rootScope.$broadcast('PAGING_UPDATE', {total: data.totalSamples});
        updateSelectedCount(data.count);
      });
    };

    svc.updateSample = function (s) {
      var t = s.selected ? addSample(s) : removeSample(s);
      t.then(function (data) {
        doUIUpdates(data);
      });
    };

    svc.updateFile = function (s, f) {
      var t = f.selected ? addFile(s, f) : removeFile(s, f);
      t.then(function (data) {
        doUIUpdates(data);
      });
    };

    svc.getSelectedSampleNames = function () {
      return base.get("cart/names").then(function (data) {
        return data.samples;
      });
    };

    svc.merge = function (params) {
      return base.customPOST(params, 'merge').then(function (data) {
        if (data.result === 'success') {
          svc.getSamples({});
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

    function doUIUpdates(data) {
      updateSample(data.sample);
      updateSelectedCount(data.count);
    }

    function addSample(s) {
      return base.customPOST({sampleId: s.id}, 'cart/add/sample');
    }

    function removeSample(s) {
      return base.customPOST({sampleId: s.id}, 'cart/remove/sample');
    }

    function addFile(s, f) {
      return base.customPOST({sampleId: s.id, fileId: f.id}, 'cart/add/file');
    }

    function removeFile(s, f) {
      return base.customPOST({sampleId: s.id, fileId: f.id}, 'cart/remove/file');
    }

    function updateSample(s) {
      var i = _.findKey(svc.samples, {id: s.id});
      svc.samples[i] = s;
    }

    function copyMoveSamples(projectId, move) {
      return base.customPOST({
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
          svc.getSamples({});
        }
      });
    }

    function updateSelectedCount(count) {
      $rootScope.$broadcast('COUNT', {count: count});
    }
  }

  /*[- */
// Handles everything to do with paging for the Samples Table
// @param $scope Scope the controller is responsible for
// @param SamplesService Server handler for samples.
  /* -]*/
  function PagingCtrl($scope, SamplesService) {
    "use strict";
    var vm = this;
    vm.count = 10;
    vm.total = 0;
    vm.page = 1;

    vm.update = function () {
      SamplesService.getSamples({page: vm.page - 1, count: vm.count});
    };

    $scope.$on('PAGING_UPDATE', function (e, args) {
      vm.total = args.total;
    });
  }

  /*[- */
// Responsible for all samples within the table
// @param SamplesService Server handler for samples.
  /* -]*/
  function SamplesTableCtrl(SamplesService) {
    "use strict";
    var vm = this;
    vm.open = [];

    vm.samples = SamplesService.samples;

    vm.updateSample = function (s) {
      SamplesService.updateSample(s);
    };

    vm.updateFile = function (s, f) {
      SamplesService.updateFile(s, f);
    };

    SamplesService.getSamples({});
  }

  function SubNavCtrl($scope, $modal, BASE_URL, SamplesService) {
    "use strict";
    var vm = this;
    vm.count = 0;

    vm.merge = function () {
      $modal.open({
        templateUrl: BASE_URL + 'projects/templates/merge',
        controller : 'MergeCtrl as mergeCtrl',
        resolve    : {
          samples: function () {
            return SamplesService.getSelectedSampleNames();
          }
        }
      });
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

  angular.module('Samples', ['ui.select', 'cgBusy'])
    .run(['$rootScope', setRootVariable])
    .service('Select2Service', ['$timeout', Select2Service])
    .service('SamplesService', ['$rootScope', 'Restangular', 'notifications', SamplesService])
    .controller('SubNavCtrl', ['$scope', '$modal', 'BASE_URL', 'SamplesService', SubNavCtrl])
    .controller('PagingCtrl', ['$scope', 'SamplesService', PagingCtrl])
    .controller('SamplesTableCtrl', ['SamplesService', SamplesTableCtrl])
    .controller('MergeCtrl', ['$scope', '$modalInstance', 'Select2Service', 'SamplesService', 'samples', MergeCtrl])
    .controller('CopyMoveCtrl', ['$modalInstance', '$rootScope', 'BASE_URL', 'SamplesService', 'Select2Service', 'samples', 'type', CopyMoveCtrl]);
})(angular, $, _);
