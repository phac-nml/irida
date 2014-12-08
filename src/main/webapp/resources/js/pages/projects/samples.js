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


  function PagingFilter($rootScope, filter, SamplesService) {
    "use strict";
    return function (samples) {
      // samples have already been sorted and filter based on the side bar.
      SamplesService.setFilteredSamples(samples);
      $rootScope.$broadcast('PAGING_UPDATE', {total: samples.length});
      var begin = filter.page * filter.count;
      return samples.slice(begin, begin + filter.count);
    }
  }

  function FilterFactory() {
    "use strict";
    return {
      page    : 0,
      sortDir : false,
      sortedBy: 'createdDate',
      count   : 10
    }
  }

  /*[- */
// Responsible for all server calls for samples
// @param $rootScope The root scope for the page.
// @param R Restangular
  /* -]*/
  function SamplesService($rootScope, BASE_URL, R, notifications, filter) {
    "use strict";
    var svc = this,
        id = angular.element("#projectId").val(),
        base = R.all('projects/' + id + '/ajax/samples'),
        selected = [],
        filtered = [];
    svc.samples = [];

    svc.getSamples = function (f) {
      _.extend(svc.filter, f || {});
      $rootScope.cgPromise = base.customGET("").then(function (data) {
        angular.copy(data.samples, svc.samples);
        $rootScope.$broadcast('SAMPLES_INIT', {total: data.samples.length});
      });
    };

    svc.getNumSamples = function () {
      return svc.samples.length;
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

    svc.getProjectId = function () {
      return id;
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
      var begin = filter.page * filter.count;
      _.each(filtered.slice(begin, begin + filter.count), function (s) {
        if (!s.selected) {
          s.selected = true;
          selected.push(s);
        }
      });
      updateSelectedCount();
    };

    svc.selectAll = function () {
      _.each(filtered, function (s) {
        s.selected = true;
        if (!_.contains(selected, s)) {
          selected.push(s);
        }
      });
      updateSelectedCount();
    };

    svc.selectNone = function () {
      _.each(svc.samples, function (s) {
        s.selected = false
      });
      selected = [];
      updateSelectedCount();
    };

    svc.downloadFiles = function () {
      var ids = getSelectedSampleIds();
      var mapped = _.map(ids, function (id) {
        return "ids=" + id
      });
      var iframe = document.createElement("iframe");
      iframe.src = BASE_URL + "projects/" + id + "/download/files?" + mapped.join("&");
      iframe.style.display = "none";
      document.body.appendChild(iframe);
    };

    svc.galaxyUpload = function (email, name) {
      return base.customPOST({
        email: email,
        name: name,
        sampleIds: getSelectedSampleIds()
      }, 'galaxy/upload').then(function(data) {
        if(data.result === 'success') {
          notifications.show({msg: data.msg});
        }
        return data;
      })
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

  function sortBy() {
    'use strict';
    return {
      template  : '<a class="clickable" ng-click="sort(sortValue)">' +
      '<span ng-transclude=""></span>' +
      '<span class="pull-right" ng-show="sortedby == sortvalue">' +
      '<i class="fa fa-fw" ng-class="{true: \'fa-sort-asc\', false: \'fa-sort-desc\'}[sortdir]"></i>' +
      '</span><span class="pull-right" ng-show="sortedby != sortvalue"><i class="fa fa-sort fa-fw"></i></span></a>',
      restrict  : 'EA',
      transclude: true,
      replace   : false,
      scope     : {
        sortdir  : '=',
        sortedby : '=',
        sortvalue: '@',
        onsort   : '='
      },
      link      : function (scope) {
        scope.sort = function () {
          if (scope.sortedby === scope.sortvalue) {
            scope.sortdir = !scope.sortdir;
          }
          else {
            scope.sortedby = scope.sortvalue;
            scope.sortdir = true;
          }
          scope.onsort(scope.sortedby, scope.sortdir);
        };
      }
    };
  }

  /*[- */
// Handles everything to do with paging for the Samples Table
// @param $scope Scope the controller is responsible for
// @param SamplesService Server handler for samples.
  /* -]*/
  function PagingCtrl($scope, filter) {
    "use strict";
    var vm = this;
    vm.count = 10;
    vm.total = 0;
    vm.page = 1;

    vm.update = function () {
      filter.page = vm.page - 1;
    };

    $scope.$on('PAGING_UPDATE', function (e, args) {
      vm.total = args.total;
    });

    $scope.$on('PAGE_CHANGE', function (e, args) {
      vm.page = args.page;
      vm.update();
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
        vm.selection.isopen = false;
        SamplesService.selectPage();
      },
      selectAll : function selectAll() {
        vm.selection.isopen = false;
        SamplesService.selectAll();
      },
      selectNone: function selectNone() {
        vm.selection.isopen = false;
        SamplesService.selectNone();
      }
    };

    vm.samplesOptions = {
      open: false
    };

    vm.export = {
      open  : false,
      download: function download() {
        vm.export.open = false;
        SamplesService.downloadFiles();
      },
      linker: function linker() {
        vm.export.open = false;
        $modal.open({
          templateUrl: BASE_URL + 'projects/templates/samples/linker',
          controller : 'LinkerCtrl as lCtrl',
          resolve    : {
            samples  : function () {
              return SamplesService.getSelectedSampleNames();
            },
            projectId: function () {
              return SamplesService.getProjectId();
            }
          }
        });
      },
      galaxy: function galaxy() {
        vm.export.open = false;
        $modal.open({
          templateUrl: BASE_URL + 'projects/' + SamplesService.getProjectId() + '/templates/samples/galaxy',
          controller: 'GalaxyCtrl as gCtrl'
        });
      }
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

  function LinkerCtrl($modalInstance, SamplesService) {
    "use strict";
    var vm = this;
    vm.samples = SamplesService.getSelectedSampleNames();
    vm.projectId = SamplesService.getProjectId();
    vm.total = SamplesService.samples.length;

    vm.close = function () {
      $modalInstance.close();
    };
  }

  function SortCtrl($rootScope, filter) {
    "use strict";
    var vm = this;
    vm.filter = filter;

    vm.onSort = function (sortedBy, sortDir) {
      vm.filter.sortedBy = sortedBy;
      vm.filter.sortDir = sortDir;
      $rootScope.$broadcast('PAGE_CHANGE', {page: 1});
    }
  }

  function FilterCtrl($scope, filter) {
    "use strict";
    var vm = this;
    vm.filter = filter;
    vm.name = "";

    $scope.$watch(function () {
      return vm.name;
    }, _.debounce(function (n, o) {
      if (n !== o) {
        filter.name = vm.name;
        $scope.$apply();
      }
    }, 500));

    $scope.$watch(function () {
      return vm.organism;
    }, _.debounce(function (n, o) {
      if (n !== o) {
        if (vm.organism.length > 0) {
          filter.organism = vm.organism;
        }
        else {
          delete filter.organism;
        }
        $scope.$apply();
      }
    }, 500));

    $scope.$on('SAMPLES_INIT', function (e, args) {
      vm.total = args.total;
    });

    $scope.$on('PAGING_UPDATE', function (e, args) {
      vm.count = args.total;
    });
  }

  function GalaxyCtrl($timeout, $modalInstance, SamplesService) {
    "use strict";
    var vm = this;

    vm.upload = function () {
      vm.uploading = true;
      SamplesService.galaxyUpload(vm.email, vm.name).then(function(data) {
        vm.uploading = false;
        if(data.result === 'success') {
          vm.close();
          // TODO: Create a progress bar to monitor the status of the upload.
        }
        else {
          vm.errors = data.errors;
        }
      });
    };

    vm.setName = function (name) {
      vm.name = name;
    };

    vm.setEmail = function (email) {
      vm.email = email;
    };

    vm.close = function () {
      $modalInstance.close();
    };
  }

  angular.module('Samples', ['cgBusy'])
    .run(['$rootScope', setRootVariable])
    .factory('FilterFactory', [FilterFactory])
    .service('Select2Service', ['$timeout', Select2Service])
    .service('SamplesService', ['$rootScope', 'BASE_URL', 'Restangular', 'notifications', 'FilterFactory', SamplesService])
    .filter('PagingFilter', ['$rootScope', 'FilterFactory', 'SamplesService', PagingFilter])
    .directive('sortBy', [sortBy])
    .controller('SubNavCtrl', ['$scope', '$modal', 'BASE_URL', 'SamplesService', SubNavCtrl])
    .controller('PagingCtrl', ['$scope', 'FilterFactory', PagingCtrl])
    .controller('SamplesTableCtrl', ['SamplesService', 'FilterFactory', SamplesTableCtrl])
    .controller('MergeCtrl', ['$scope', '$modalInstance', 'Select2Service', 'SamplesService', 'samples', MergeCtrl])
    .controller('CopyMoveCtrl', ['$modalInstance', '$rootScope', 'BASE_URL', 'SamplesService', 'Select2Service', 'samples', 'type', CopyMoveCtrl])
    .controller('SelectedCountCtrl', ['$scope', SelectedCountCtrl])
    .controller('LinkerCtrl', ['$modalInstance', 'SamplesService', LinkerCtrl])
    .controller('SortCtrl', ['$rootScope', 'FilterFactory', SortCtrl])
    .controller('FilterCtrl', ['$scope', 'FilterFactory', FilterCtrl])
    .controller('GalaxyCtrl', ['$timeout', '$modalInstance', 'SamplesService', GalaxyCtrl])
  ;
})(angular, $, _);
