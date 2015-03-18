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
    }

    getCart(true);
  }

  /**
   * Controller for functions on the cart slider
   * @param CartService The cart service to communicate with the server
   */
    function CartSliderController(CartService){
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
    };

      svc.removeProject = function(projectId){
        return $http.delete(urls.project+projectId).then(function () {
          scope.$broadcast("cart.update", {});
        })
      };
      return [sampleFormEntity];
    };

      svc.removeSample = function(projectId,sampleId){
        return $http.delete(urls.project+projectId+"/samples/"+sampleId).then(function () {
          scope.$broadcast("cart.update", {});
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

  function GalaxyCartDialogCtrl($modalInstance, $timeout, $scope, CartService, GalaxyExportService, openedByCart, multiProject) {
    "use strict";
    var vm = this;
    vm.showOauthIframe = false;
    vm.showEmailLibInput = true;
    vm.redirectURI = TL.BASE_URL + "galaxy/auth_code";

    vm.upload = function () {
      vm.makeOauth2AuthRequest("webClient");
      vm.showEmailLibInput = false;
      vm.showOauthIframe = true;
    };

    vm.makeOauth2AuthRequest = function (clientID) {
      var request = buildOauth2Request(clientID, "code", "read", vm.redirectURI);
      vm.iframeSrc = "/api/oauth/authorize" + request;
    }

    function buildOauth2Request(clientID, responseType, scope, redirectURI) {
      //Is there an URL builder I should use?
      return "?&client_id=" + clientID + "&response_type=" + responseType + "&scope=" + scope + "&redirect_uri=" + redirectURI;
    }

    $scope.$on("galaxyAuthCode", function (e, authToken) {

      if (authToken) {
        vm.uploading = true;

        vm.showOauthIframe = false;
        vm.showEmailLibInput = true;

        if (openedByCart) {
          CartService.clear()
          GalaxyExportService.exportFromCart(vm.name, vm.email, authToken, vm.redirectURI).then(sendSampleForm);
        }
        else {
          sendSampleForm(GalaxyExportService.exportFromProjSampPage(vm.name, vm.email, authToken, vm.redirectURI));
        }
      }
    });

    function sendSampleForm(sampleFormEntities) {
      vm.sampleFormEntities = sampleFormEntities;

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

    angular
      .module('irida.cart', [])
      .service('CartService', ['$rootScope', '$http', '$q', CartService])
      .controller('CartSliderController', ['CartService', CartSliderController])
      .directive('cart', [CartDirective])
    ;
})();