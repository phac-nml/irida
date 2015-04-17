(function () {
    "use strict";

    function CartController($scope, $timeout, cart) {
        "use strict";
        var vm = this,
          initialized = false;
        vm.show = false;
        vm.projects = [];
        vm.count = 0;
        vm.collapsed = {};

        $scope.$on('cart.update', function () {
            getCart(false);
        });

        function getCart (collapse) {
            cart.all()
              .then(function (data) {
                var prev = vm.count;
                vm.count = 0;
                  vm.projects = data;
                  _.each(data, function(p) {
                      vm.count += p.samples.length;
                      if(collapse){
                        vm.collapsed[p.id] = true;
                      }
                  });
                if (initialized && prev !== vm.count) {
                  vm.animation = 'glow';
                  $timeout(function () {
                    vm.animation = '';
                  }, 3000);
                } else {
                  // This is just to prevent animation on page load.
                  initialized = true;
                }
              });
        }

        getCart(true);
    }

  /**
   * Controller for functions on the cart slider
   * @param CartService The cart service to communicate with the server
   */
    function CartSliderController(CartService, $modal) {
      "use strict";

      var vm = this;

      vm.clear = function(){
        CartService.clear();
      };

      vm.removeProject = function(projectId){
        CartService.removeProject(projectId);
      }

      vm.removeSample = function(projectId,sampleId){
        CartService.removeSample(projectId,sampleId);
      }

    vm.exportToGalaxy = function () {
      CartService.all()
        .then(function (data) {
          if (data != null) {
            var firstProjID = data[0].id

            $modal.open({
              templateUrl: TL.BASE_URL + 'cart/template/galaxy/project/' + firstProjID,
              controller : 'GalaxyDialogCtrl as gCtrl',
              resolve    : {
                openedByCart: function () {
                  return true;
                },
                multiProject: function () {
                  return (data.length > 1)
                }
              }
            });
          }
        });
    }
    }

    function CartDirective() {
        return {
            restrict    : "E",
            templateUrl : "/cart.html",
            replace: true,
            controllerAs: "cart",
            controller  : ['$scope', '$timeout', 'CartService', CartController]
        }
    }

    function CartService(scope, $http, $q) {
        var svc = this,
            urls = {
                all: TL.BASE_URL + "cart",
                add: TL.BASE_URL + "cart/add/samples",
                project: TL.BASE_URL + "cart/project/"
            };

        svc.all = function () {
            return $http.get(urls.all)
              .then(function (response) {
                  if (response.data) {
                      return response.data.projects
                  }
                  else {
                      return [];
                  }
              });
        };

        svc.add = function (samples) {
          var promises = [];

          _.forEach(samples, function(s) {
            promises.push($http.post(urls.add, {projectId: s.project, sampleIds: [s.sample]}));
          });

          $q.all(promises).then(function(){
            scope.$broadcast("cart.update", {});
          });
        };

      svc.clear = function () {
        //fire a DELETE to the server on the cart then broadcast the cart update event
        return $http.delete(urls.all).then(function () {
          scope.$broadcast("cart.update", {});
        });
      };

      svc.removeProject = function(projectId){
        return $http.delete(urls.project+projectId).then(function () {
          scope.$broadcast("cart.update", {});
        })
      };

      svc.removeSample = function(projectId,sampleId){
        return $http.delete(urls.project+projectId+"/samples/"+sampleId).then(function () {
          scope.$broadcast("cart.update", {});
        })
      }

    }

  function GalaxyExportService(CartService, StorageService) {
    "use strict";
    var svc = this;
    var params = {};
    var samples = [];


    function initialize(libraryName, email, authCode, redirectURI) {
      params = {
        "_embedded": {
          "library": {"name": libraryName},
          "user"   : {"email": email},
          "oauth2" : {
            "code"    : authCode,
            "redirect": redirectURI
          },
          "samples": samples
        }
      };
    };

    function addSampleFile(sampleName, sampleFilePath) {
      var sample = _.find(samples, function (sampleItr) {
        return (sampleItr.name == sampleName)
      });
      if (sample == null) {
        sample = {
          "name"     : sampleName,
          "_links"   : {"self": {"href": ""}}, //expected by the tool
          "_embedded": {"sample_files": []}
        };
        samples.push(sample);
      }
      sample._embedded.sample_files.push({'_links': {'self': {'href': sampleFilePath}}});
    };

    function getSampleFormEntities() {
      var sampleFormEntity = {
        "name" : "json_params",
        "value": JSON.stringify(params)
      };
      return [sampleFormEntity];
    };

    svc.exportFromProjSampPage = function (libraryName, email, authCode, redirectURI) {
      initialize(libraryName, email, authCode, redirectURI);

      var samples = StorageService.getSamples()
      _.each(samples, function (sample) {
        _.each(sample.embedded.sample_files, function (sequenceFile) {
          addSampleFile(sample.sample.label, sequenceFile._links.self.href)
        })
      });
      return getSampleFormEntities();
    };

    svc.exportFromCart = function (libraryName, email, authCode, redirectURI) {
      return CartService.all()
        .then(function (data) {
          initialize(libraryName, email, authCode, redirectURI);
          var projects = data;
          _.each(projects, function (project) {
            var samples = project.samples;
            _.each(samples, function (sample) {
              var sequenceFiles = sample.sequenceFiles
              _.each(sequenceFiles, function (sequenceFile) {
                addSampleFile(sample.label, sequenceFile.selfRef);
              })
            })
          });
          return getSampleFormEntities();
        });
    };
  }

  function GalaxyDialogCtrl($modalInstance, $timeout, $scope, CartService, GalaxyExportService, openedByCart, multiProject) {
    "use strict";
    var vm = this;
    vm.showOauthIframe = false;
    vm.showEmailLibInput = true;
    vm.redirectURI = TL.BASE_URL + "galaxy/auth_code";

    vm.upload = function () {
      vm.makeOauth2AuthRequest(TL.galaxyClientID);
      vm.showEmailLibInput = false;
      vm.showOauthIframe = true;
    };

    vm.makeOauth2AuthRequest = function (clientID) {
      var request = buildOauth2Request(clientID, "code", "read", vm.redirectURI);
      vm.iframeSrc = TL.BASE_URL + "api/oauth/authorize" + request;
    }

    function buildOauth2Request(clientID, responseType, scope, redirectURI) {
      return "?&client_id=" + clientID + "&response_type=" + responseType + "&scope=" + scope + "&redirect_uri=" + redirectURI;
    }

    $scope.$on("galaxyAuthCode", function (e, authToken) {

      if (authToken) {
        vm.uploading = true;

        vm.showOauthIframe = false;
        vm.showEmailLibInput = true;

        if (openedByCart) {
          GalaxyExportService.exportFromCart(vm.name, vm.email, authToken, vm.redirectURI).then(sendSampleForm);
        }
        else {
          sendSampleForm(GalaxyExportService.exportFromProjSampPage(vm.name, vm.email, authToken, vm.redirectURI));
        }
      }
    });

    function sendSampleForm(sampleFormEntities) {
      vm.sampleFormEntities = sampleFormEntities;

      if(openedByCart) {
        CartService.clear();
      }

      //$timeout is necessary because it adds a new event to the browser event queue,
      //allowing the full form to be generated before it is submitted.

      //Two timeouts are required now--this is starting to look like a real hack.
      $timeout(function () {
        $timeout(function () {
          document.getElementById("galSubFrm").submit();
          vm.close();
        });
      });
    }

    vm.setName = function (name, orgName) {
      if (multiProject) {
        vm.name = orgName;
      }
      else {
        vm.name = name;
      }
    };
    vm.setEmail = function (email) {
      vm.email = email;
    };
    vm.close = function () {
      $modalInstance.close();
    };

  }

  angular
      .module('irida.cart', [])
      .service('CartService', ['$rootScope', '$http', '$q', CartService])
    .controller('CartSliderController', ['CartService', '$modal', CartSliderController])
    .controller('GalaxyDialogCtrl', ['$modalInstance', '$timeout', '$scope', 'CartService','GalaxyExportService', 'openedByCart', 'multiProject', GalaxyDialogCtrl])
    .service('GalaxyExportService', ['CartService', 'StorageService', GalaxyExportService])
      .directive('cart', [CartDirective])
    ;
})();