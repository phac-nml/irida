(function (angular, _, $, TL) {
  'use strict';

  function CartController($scope, $timeout, cart) {
    var vm = this;
    vm.show = false;
    vm.projects = [];
    vm.remote = [];
    vm.count = 0;
    vm.collapsed = {};
    vm.term = '';

    $scope.$on('cart.update', function () {
      getCart(false);
    });

    function getCart(collapse) {
      cart.all()
        .then(function (data) {
          vm.count = 0;
          vm.projects = data.projects;
          vm.remote = data.remote;
          _.each(vm.projects, function (p) {
            vm.count += p.samples.length;
            // Sort the samples by created date.
            p.samples.sort(function (a, b) {
              return b.createdDate -a.createdDate > 0;
            });
            if (collapse) {
              vm.collapsed[p.id] = true;
            }
          });
          vm.count += vm.getRemoteCount();
          if (collapse) {
            vm.collapsed.remote = true;
          }
        });
    }

    vm.getRemoteCount = function () {
      return Object.keys(vm.remote).length;
    };

    getCart(true);
  }

  /**
   * Controller for functions on the cart slider
   * @param CartService The cart service to communicate with the server
   */
  function CartSliderController(CartService, $uibModal) {
    var vm = this;

    vm.clear = function () {
      CartService.clear();
    };

    vm.removeProject = function (projectId) {
      CartService.removeProject(projectId);
    };

    vm.removeRemoteSamples = function () {
      CartService.removeRemoteSamples();
    };

    vm.removeRemoteSample = function (sampleUrl) {
      CartService.removeRemoteSample(sampleUrl);
    };

    vm.removeSample = function (projectId, sampleId) {
      CartService.removeSample(projectId, sampleId);
    };

    vm.exportToGalaxy = function () {
      CartService.all()
        .then(function (data) {
          if (data !== null) {
            var firstProjID = data.projects[0].id;

            $uibModal.open({
              templateUrl: TL.BASE_URL + 'cart/template/galaxy/project/' + firstProjID,
              controller: 'GalaxyDialogCtrl as gCtrl',
              resolve: {
                openedByCart: function () {
                  return true;
                },
                multiProject: function () {
                  return (data.length > 1);
                },
                sampleIds: function () {
                  return false;
                },
                projectId: function () {
                  return false;
                }
              }
            });
          }
        });
    };
  }

  function CartDirective() {
    return {
      restrict: 'E',
      templateUrl: '/cart.html',
      replace: true,
      controllerAs: 'cart',
      controller: ['$scope', '$timeout', 'CartService', CartController]
    };
  }

  function CartService(scope, $http, $q, notifications) {
    var svc = this,
      urls = {
        all: TL.BASE_URL + 'cart',
        add: TL.BASE_URL + 'cart/add/samples',
        addRemote: TL.BASE_URL + 'cart/add/samples/remote',
        removeRemote: TL.BASE_URL + 'cart/remove/samples/remote',
        project: TL.BASE_URL + 'cart/project/'
      };

    svc.all = function () {
      return $http.get(urls.all)
        .then(function (response) {
          if (response.data) {
            return {projects: response.data.projects, remote: response.data.remote};
          } else {
            return [];
          }
        });
    };

    svc.add = function(items) {
      var ids = Object.keys(items);
      var defer = $q.defer;
      var promises = [];
      var resultMsg = [];

      ids.forEach(function(id) {
        var promise = $http.post(urls.add, {projectId: id, sampleIds: items[id]})
          .then(function(result) {
            resultMsg.push(result.data.message);
          });
        promises.push(promise);
      });

      return $q.all(promises).then(function() {
        scope.$emit('cart.update');
        resultMsg.forEach(function(msg) {
          notifications.show({msg: msg});
        });
      });
    };

    svc.clear = function () {
      //fire a DELETE to the server on the cart then broadcast the cart update event
      return $http.delete(urls.all).then(function () {
        scope.$broadcast('cart.update', {});
      });
    };

    svc.removeProject = function (projectId) {
      return $http.delete(urls.project + projectId).then(function () {
        scope.$broadcast('cart.update', {});
      });
    };

    svc.removeSample = function (projectId, sampleId) {
      return $http.delete(urls.project + projectId + '/samples/' + sampleId).then(function () {
        scope.$broadcast('cart.update', {});
      });
    };

    svc.removeRemoteSamples = function () {
      return $http.delete(urls.removeRemote).then(function () {
        scope.$broadcast('cart.update', {});
      });
    };

    svc.removeRemoteSample = function (sampleURL) {
      return $http.post(urls.removeRemote, {sampleURL: sampleURL}).then(function () {
        scope.$broadcast('cart.update', {});
      });
    };

  }

  function GalaxyExportService(CartService, $http) {
    var svc = this,
      samples = [];

    function addSampleFile(sampleName, sampleFilePath) {
      var sample = _.find(samples, function (sampleItr) {
        return (sampleItr.name === sampleName);
      });
      if (typeof sample === 'undefined') {
        sample = {
          'name': sampleName,
          '_links': {'self': {'href': ''}}, //expected by the tool
          '_embedded': {'sample_files': []}
        };
        samples.push(sample);
      }
      sample._embedded.sample_files.push({'_links': {'self': {'href': sampleFilePath}}});
    }

    function getSampleFormEntities(args) {
      var defaults = {
          addtohistory: false,
          makepairedcollection: false
        },
        p = _.extend({}, defaults, args),
        params = {
        '_embedded': {
          'library': {'name': p.name},
          'user': {'email': p.email},
          'addtohistory': p.addtohistory,
          'makepairedcollection': p.makepairedcollection,
          'oauth2': {
            'code': p.authToken,
            'redirect': p.redirectURI
          },
          'samples': samples
        }
      };
      return [{
        'name': 'json_params',
        'value': JSON.stringify(params)
      }];
    }

    svc.exportFromProjSampPage = function (args, ids, projectId) {
      // Need to get an actual list of samples from the server from their ids.
      return $http.get(PAGE.urls.samples.idList + "?" + $.param({sampleIds: ids, projectId: projectId}))
        .then(function (result) {
          _.each(result.data.mapList, function (sample) {
              addSampleFile(sample.label, sample.href);
          });
          return getSampleFormEntities(args);
        });
    };

    svc.exportFromCart = function (args) {
      return CartService.all()
        .then(function (data) {
          var projects = data.projects;
          _.each(projects, function (project) {
            var samples = project.samples;
            _.each(samples, function (sample) {
              addSampleFile(sample.label, sample.href);
            });
          });
          return getSampleFormEntities(args);
        });
    };
  }

  function GalaxyDialogCtrl($uibModalInstance, $timeout, $scope, CartService, GalaxyExportService, openedByCart, multiProject, sampleIds, projectId) {
    var vm = this;
    vm.addtohistory=true;
    vm.makepairedcollection=true;
    vm.showOauthIframe = false;
    vm.showEmailLibInput = true;
    vm.redirectURI = TL.BASE_URL + 'galaxy/auth_code';

    vm.upload = function () {
      vm.makeOauth2AuthRequest(TL.galaxyClientID);
      vm.showEmailLibInput = false;
      vm.showOauthIframe = true;
    };

    vm.makeOauth2AuthRequest = function (clientID) {
      var request = buildOauth2Request(clientID, 'code', 'read', vm.redirectURI);
      vm.iframeSrc = TL.BASE_URL + 'api/oauth/authorize' + request;
    };

    function buildOauth2Request(clientID, responseType, scope, redirectURI) {
      return '?&client_id=' + clientID + '&response_type=' + responseType + '&scope=' + scope + '&redirect_uri=' + redirectURI;
    }

    $scope.$on('galaxyAuthCode', function (e, authToken) {

      if (authToken) {
        vm.uploading = true;

        vm.showOauthIframe = false;
        vm.showEmailLibInput = true;

        if (openedByCart) {
          GalaxyExportService.exportFromCart({name: vm.name, email: vm.email, addtohistory: vm.addtohistory, makepairedcollection: vm.makepairedcollection, authToken: authToken, redirectURI: vm.redirectURI}).then(sendSampleForm);
        } else {
          GalaxyExportService.exportFromProjSampPage({name: vm.name, email: vm.email, addtohistory: vm.addtohistory, makepairedcollection: vm.makepairedcollection, authToken: authToken, redirectURI: vm.redirectURI}, sampleIds, projectId).then(sendSampleForm);
        }
      }
    });

    function sendSampleForm(sampleFormEntities) {
      vm.sampleFormEntities = sampleFormEntities;

      if (openedByCart) {
        CartService.clear();
      }

      //$timeout is necessary because it adds a new event to the browser event queue,
      //allowing the full form to be generated before it is submitted.

      //Two timeouts are required now--this is starting to look like a real hack.
      $timeout(function () {
        $timeout(function () {
          document.getElementById('galSubFrm').submit();
          vm.close();
        });
      });
    }

    vm.setName = function (name, orgName) {
      if (multiProject) {
        vm.name = orgName;
      } else {
        vm.name = name;
      }
    };
    vm.setEmail = function (email) {
      vm.email = email;
    };
    vm.close = function () {
      $uibModalInstance.close();
    };

  }

  /**
   * @name cartFilter
   * @desc Filters the list of projects in the cart based on the term provided in the search input.
   * @type {Filter}
   */
  function CartFilter() {
    /**
     * @param list - list to filter
     * @param term - search term to use to filter the list.
     */
    return function (list, term) {
      //filter each element in the collection
      return _.filter(list, function (item) {
        //if we have a project, check for how many samples we have left
        if (item.samples) {
          return filterList(item.samples, term).length > 0;
        }
        //if we have an individual sample, filter it
        return filterSample(item, term);

      });
    };

    function filterList(samples, term) {
      return _.filter(samples, function (s) {
        return filterSample(s, term);
      });
    }

    function filterSample(item, term) {
      term = term.toLowerCase();
      return item.label.toLowerCase().indexOf(term) > -1;
    }

  }

  angular
    .module('irida.cart', ["irida.notifications"])
    .service('CartService', ['$rootScope', '$http', '$q', "notifications", CartService])
    .controller('CartSliderController', ['CartService', '$uibModal', CartSliderController])
    .controller('GalaxyDialogCtrl', ['$uibModalInstance', '$timeout', '$scope', 'CartService', 'GalaxyExportService', 'openedByCart', 'multiProject', 'sampleIds', 'projectId', GalaxyDialogCtrl])
    .service('GalaxyExportService', ['CartService', '$http', GalaxyExportService])
    .directive('cart', [CartDirective])
    .filter('cartFilter', [CartFilter])
  ;
})(window.angular, window._, window.jQuery, window.TL);
