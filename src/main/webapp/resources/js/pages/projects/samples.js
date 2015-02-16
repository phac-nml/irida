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


  /**
   * Handles filtering of samples via the sidebar
   * @param filter current filter value
   */
  function SamplesFilter(filter) {
    function _filterEntry(sample) {
      var result = true;
      _.forOwn(filter.sample, function (value, key) {
        if (sample[key] === null || sample[key].toLowerCase().indexOf(value.toLowerCase()) < 0) {
          result = false;
        }
      });
      return result;
    }

    return function (entries) {
      return _.filter(entries, function (entry) {
        var filtered = _filterEntry(entry.sample);

        return filtered;
      });
    }
  }

  function FilterFactory() {
    "use strict";
    return {
      page    : 0,
      sortDir : false,
      sortedBy: 'sample.createdDate',
      count   : 10,
      sample  : {}
    }
  }

  function StorageService() {
    "use strict";
    //storing samples as an object hash
    var storage = {};

    function addSample(sample) {
      storage[sample.id] = sample;
    }

    function removeSample(id) {
      delete storage[id];
    }

    function getKeys() {
      return Object.keys(storage);
    }

    function clear() {
      storage = {};
    }

    function removeUnavailableSamples(available) {
      var newStorage = {};
      _.forEach(available, function (sample) {
        if (storage[sample.id] != null) {
          newStorage[sample.id] = storage[sample.id];
        }
      });

      storage = newStorage;
    }

    function getSamples() {
      return storage;
    }

    return ({
      addSample               : addSample,
      removeSample            : removeSample,
      getKeys                 : getKeys,
      getSamples              : getSamples,
      clear                   : clear,
      removeUnavailableSamples: removeUnavailableSamples
    });
  }

  /*[- */
// Responsible for all server calls for samples
// @param $rootScope The root scope for the page.
// @param R Restangular
  /* -]*/
  function SamplesService($rootScope, storage, R, notifications, filter, $q) {
    "use strict";
    var svc = this,
        base = R.all('projects/' + project.id),
        filtered = [];
    svc.samples = [];

    svc.getNumSamples = function () {
      return svc.samples.length;
    };

    svc.setFilteredSamples = function (f) {
      filtered = f;
    };

    svc.updateSample = function (s) {
      if (s.selected) {
        storage.addSample(s);
      }
      else {
        storage.removeSample(s.id);
      }
      updateSelectedCount()
    };

    svc.getSelectedSampleNames = function () {
      return storage.getSamples();
    };

    svc.merge = function (params) {
      params.sampleIds = getSelectedSampleIds();
      return base.customPOST(params, 'ajax/samples/merge').then(function (data) {
        if (data.result === 'success') {
          $rootScope.$broadcast("SAMPLE_CONTENT_MODIFIED");
          storage.clear();
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
          storage.addSample(s);
        }
      });
      updateSelectedCount();
    };

    svc.selectAll = function () {
      _.each(filtered, function (s) {
        s.selected = true;
        storage.addSample(s);
      });
      updateSelectedCount();
    };

    svc.selectNone = function () {
      _.each(svc.samples, function (s) {
        s.selected = false
      });
      storage.clear();
      updateSelectedCount();
    };

    svc.downloadFiles = function () {
      var ids = getSelectedSampleIds();
      var mapped = _.map(ids, function (id) {
        return "ids=" + id
      });
      var iframe = document.createElement("iframe");
      iframe.src = TL.BASE_URL + "projects/" + id + "/download/files?" + mapped.join("&");
      iframe.style.display = "none";
      document.body.appendChild(iframe);
    };

    svc.galaxyUpload = function (email, name) {
      return base.customPOST({
        email    : email,
        name     : name,
        sampleIds: getSelectedSampleIds()
      }, 'galaxy/upload').then(function (data) {
        if (data.result === 'success') {
          notifications.show({msg: data.msg});
        }
        return data;
      })
    };

    svc.updateSampleCount = function () {
      $rootScope.sampleCount = svc.samples.length;
    }

    /**
     * Get the currently loaded samples
     * @returns {Array} of samples
     */
    svc.getSamples = function () {
      var selectedKeys = storage.getKeys();
      $rootScope.$broadcast('SELECTED_COUNT', {count: selectedKeys.length});

      _.each(svc.samples, function (s) {
        if (_.contains(selectedKeys, s.id + "")) {
          s.selected = true;
        }
      });

      $rootScope.$broadcast('SAMPLES_INIT', {total: svc.samples.length});
      return svc.samples;
    }

    /**
     * Load a set of samples from the server.  Fires a SAMPLES_READY event on complete
     * @param getLocal Load local samples
     * @param getAssociated Load associated samples
     */
    svc.loadSamples = function (getLocal, getAssociated) {
      var samplePromises = [];
      svc.samples = [];

      if (getLocal) {
        samplePromises.push(getLocalSamples());
      }
      if (getAssociated) {
        samplePromises.push(getAssociatedSamples());
      }

      return $q.all(samplePromises).then(function (response) {
        _.forEach(response, function (p) {
          svc.samples = svc.samples.concat(p);
          svc.updateSampleCount();
        });

        $rootScope.$broadcast('SAMPLES_READY', true);

        storage.removeUnavailableSamples(svc.samples);
        updateSelectedCount();
      });
    }

    function getSelectedSampleIds() {
      return storage.getKeys();
    }

    function copyMoveSamples(projectId, move) {

      return base.customPOST({
        sampleIds         : getSelectedSampleIds(),
        newProjectId      : projectId,
        removeFromOriginal: move
      }, "/ajax/samples/copy").then(function (data) {
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

          //clear storage after moving
          storage.clear();
          updateSelectedCount();
          svc.updateSampleCount();
        }
      });
    }

    function updateSelectedCount() {
      $rootScope.$broadcast('SELECTED_COUNT', {count: storage.getKeys().length});
    }


    function getLocalSamples(f) {
      _.extend(svc.filter, f || {});
      return base.customGET('ajax/samples').then(function (data) {
        return data.samples;
      });
    }

    function getAssociatedSamples(f) {
      _.extend(svc.filter, f || {});
      return base.customGET('associated/samples').then(function (data) {
        return data.samples;
      });
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

  function galaxyNotification($timeout, GalaxyService) {
    "use strict";
    var timer;

    function link(scope, element, attrs) {
      element.on('click', function () {
        removeElement(300);
      });

      element.on('$destroy', function () {
        $timeout.cancel(timer);
      });

      var poll = function poll() {
        timer = $timeout(function () {
          GalaxyService.poll(attrs.workerid).then(function (result) {
            scope.progress = Math.ceil(result.data.progress * 100);
            scope.title = result.data.title;
            scope.message = result.data.message;
            if (result.data.error) {
              scope.error = true;
              element.addClass('error');
            }
            else if (result.data.finished) {
              element.addClass('success');
              removeElement(2000);
            }
            else {
              poll();
            }
          });
        }, 500);
      };

      function removeElement(length) {
        $timeout(function () {
          element.addClass('remove');

          $timeout(function () {
            scope.$destroy();
            element.remove();
          }, 500);
        }, length);
      }

      poll();
    }

    return {
      templateUrl: '/template/notification.html',
      restrict   : 'E',
      replace    : true,
      scope      : {
        message: '@'
      },
      link       : link
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
    vm.count = filter.count;
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

    $scope.$on('PAGE_SIZE_CHANGE', function (e, args) {
      vm.count = filter.count;
    });
  }

  function FilterCountCtrl($rootScope, filter, SampleService) {
    var vm = this;
    vm.count = filter.count;

    vm.updateCount = function () {
      if (vm.count !== 'All') {
        filter.count = vm.count;
      } else {
        filter.count = SampleService.getNumSamples();
      }
      $rootScope.$broadcast('PAGE_SIZE_CHANGE');
    }
  }

  /*[- */
// Responsible for all samples within the table
// @param SamplesService Server handler for samples.
  /* -]*/
  function SamplesTableCtrl($rootScope, SamplesService, FilterFactory) {
    "use strict";
    var vm = this;
    vm.open = [];
    vm.filter = FilterFactory;

    vm.samples = [];

    vm.updateSample = function (s) {
      SamplesService.updateSample(s);
    };

    $rootScope.$on("SAMPLES_READY", function () {
      vm.samples = SamplesService.getSamples();
    });

  }


  /**
   * Controller for the samples display checkboxes.  This controller will ask SamplesService to load a set of samples
   * @param $rootScope page scope for sending/recieving events
   * @param SamplesService Server handler for samples.
   */
  function SampleDisplayCtrl($rootScope, SamplesService) {
    "use strict";
    var vm = this;

    //set the initial display options
    vm.displayLocal = true;
    vm.displayAssociated = false;

    vm.displaySamples = function () {
      SamplesService.loadSamples(vm.displayLocal, vm.displayAssociated);
    };

    $rootScope.$on("SAMPLE_CONTENT_MODIFIED", function () {
      vm.displaySamples();
    });

    //make initial samples load call
    vm.displaySamples();
  }

  function SubNavCtrl($scope, $modal, SamplesService) {
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
      open    : false,
      download: function download() {
        vm.export.open = false;
        SamplesService.downloadFiles();
      },
      linker  : function linker() {
        vm.export.open = false;
        $modal.open({
          templateUrl: TL.BASE_URL + 'projects/templates/samples/linker',
          controller : 'LinkerCtrl as lCtrl'
        });
      },
      galaxy  : function galaxy() {
        vm.export.open = false;
        $modal.open({
          templateUrl: TL.BASE_URL + 'projects/' + project.id + '/templates/samples/galaxy',
          controller : 'GalaxyCtrl as gCtrl'
        });
      }
    };

    vm.merge = function () {
      if (vm.count > 1) {
        $modal.open({
          templateUrl: TL.BASE_URL + 'projects/templates/merge',
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
        templateUrl: TL.BASE_URL + 'projects/templates/' + type,
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

    $scope.$on('SELECTED_COUNT', function (e, a) {
      vm.count = a.count;
    });
  }

  function MergeCtrl($scope, $modalInstance, Select2Service, SamplesService, samples) {
    "use strict";
    var vm = this;
    vm.samples = samples;
    vm.selected = Object.keys(samples)[0];
    vm.name = "";
    vm.error = {};

    Select2Service.init("#samplesSelect");

    vm.close = function () {
      $modalInstance.close();
    };

    vm.merge = function () {
      SamplesService.merge({mergeSampleId: vm.selected, newName: vm.name}).then(function () {
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

  function CopyMoveCtrl($modalInstance, $rootScope, SamplesService, Select2Service, samples, type) {
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
        url        : TL.BASE_URL + "projects/ajax/samples/available_projects",
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

    $scope.$on('SELECTED_COUNT', function (e, a) {
      vm.count = a.count;
    });
  }

  function LinkerCtrl($modalInstance, SamplesService) {
    "use strict";
    var vm = this;
    vm.samples = SamplesService.getSelectedSampleNames();
    vm.projectId = project.id;
    vm.total = SamplesService.samples.length;

    vm.close = function () {
      $modalInstance.close();
    };

    vm.areAllSelected = function(){
        if(Object.keys(vm.samples).length == SamplesService.samples.length){
          return true;
        }
        return false;
    }
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
      return vm.sampleName;
    }, _.debounce(function (n, o) {
      if (n !== o) {
        filter.sample.sampleName = vm.sampleName;
        $scope.$apply();
      }
    }, 500));

    $scope.$watch(function () {
      return vm.organism;
    }, _.debounce(function (n, o) {
      if (n !== o) {
        if (vm.organism.length > 0) {
          filter.sample.organism = vm.organism;
        }
        else {
          delete filter.sample.organism;
        }
        $scope.$apply();
      }
    }, 500));

    $scope.$on('PAGING_UPDATE', function (e, args) {
      vm.count = args.total;
    });
  }

  function GalaxyCtrl($timeout, $modalInstance, SamplesService) {
    "use strict";
    var vm = this;

    vm.upload = function () {
      vm.uploading = true;
      SamplesService.galaxyUpload(vm.email, vm.name).then(function (data) {
        vm.uploading = false;
        if (data.result === 'success') {
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

  function CartController(cart, storage) {
    "use strict";
    var vm = this;

    vm.add = function () {
      var samples = [];
      _.forEach(storage.getSamples(), function (s) {
        if (s.sampleType == "ASSOCIATED") {
          samples.push({"sample": s.id, "project": s.project.id});
        }
        else if (s.sampleType == "LOCAL") {
          samples.push({"sample": s.id, "project": project.id});
        }
      });

      cart.add(samples);
    };

    vm.clear = function(){
      cart.clear();
    };
  }

  angular.module('Samples', ['cgBusy', 'ngStorage', 'irida.cart'])
    .run(['$rootScope', setRootVariable])
    .factory('FilterFactory', [FilterFactory])
    .service('StorageService', [StorageService])
    .service('Select2Service', ['$timeout', Select2Service])
    .service('SamplesService', ['$rootScope', 'StorageService', 'Restangular', 'notifications', 'FilterFactory', '$q', SamplesService])
    .filter('SamplesFilter', ['FilterFactory', SamplesFilter])
    .filter('PagingFilter', ['$rootScope', 'FilterFactory', 'SamplesService', PagingFilter])
    .directive('sortBy', [sortBy])
    .controller('SubNavCtrl', ['$scope', '$modal', 'SamplesService', SubNavCtrl])
    .controller('PagingCtrl', ['$scope', 'FilterFactory', PagingCtrl])
    .controller('FilterCountCtrl', ['$rootScope', 'FilterFactory', 'SamplesService', FilterCountCtrl])
    .controller('SamplesTableCtrl', ['$rootScope', 'SamplesService', 'FilterFactory', SamplesTableCtrl])
    .controller('MergeCtrl', ['$scope', '$modalInstance', 'Select2Service', 'SamplesService', 'samples', MergeCtrl])
    .controller('CopyMoveCtrl', ['$modalInstance', '$rootScope', 'SamplesService', 'Select2Service', 'samples', 'type', CopyMoveCtrl])
    .controller('SelectedCountCtrl', ['$scope', SelectedCountCtrl])
    .controller('LinkerCtrl', ['$modalInstance', 'SamplesService', LinkerCtrl])
    .controller('SortCtrl', ['$rootScope', 'FilterFactory', SortCtrl])
    .controller('FilterCtrl', ['$scope', 'FilterFactory', FilterCtrl])
    .controller('GalaxyCtrl', ['$timeout', '$modalInstance', 'SamplesService', GalaxyCtrl])
    .controller('CartController', ['CartService', 'StorageService', CartController])
    .controller('SampleDisplayCtrl', ['$rootScope', 'SamplesService', SampleDisplayCtrl])
  ;
})
(angular, $, _);
