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
        $http.delete(urls.project+projectId).then(function () {
          scope.$broadcast("cart.update", {});
        })
      };

      svc.removeSample = function(projectId,sampleId){
        $http.delete(urls.project+projectId+"/samples/"+sampleId).then(function () {
          scope.$broadcast("cart.update", {});
        })
      }

    }

    angular
      .module('irida.cart', [])
      .service('CartService', ['$rootScope', '$http', '$q', CartService])
      .controller('CartSliderController', ['CartService', CartSliderController])
      .directive('cart', [CartDirective])
    ;
})();