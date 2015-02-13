(function () {
    "use strict";

    function CartController($scope, cart) {
        "use strict";
        var vm = this;
        vm.show = false;
        vm.projects = [];
        vm.count = 0;
        vm.collapsed = {};

        $scope.$on('cart.update', function () {
            getCart();
        });

        function getCart () {
            cart.all()
              .then(function (data) {
                  vm.projects = data;
                  vm.count = 0;
                  _.each(data, function(p) {
                      vm.count += p.samples.length;
                  })
              })
        }

        getCart();
    }

    function CartSliderController(CartService){
      "use strict";

      var vm = this;

      vm.clear = function(){
        CartService.clear();
      };
    }

    function CartDirective() {
        return {
            restrict    : "E",
            templateUrl : "/cart.html",
            replace: true,
            controllerAs: "cart",
            controller  : ['$scope', 'CartService', CartController]
        }
    }

    function CartService(scope, $http, $q) {
        var svc = this,
            urls = {
                all: TL.BASE_URL + "cart",
                add: TL.BASE_URL + "cart/add/samples"
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
        $http.delete(urls.all).then(function(){
          scope.$broadcast("cart.update", {});
        })
      };

    }

    angular
      .module('irida.cart', [])
      .service('CartService', ['$rootScope', '$http', '$q', CartService])
      .controller('CartSliderController', ['CartService', CartSliderController])
      .directive('cart', [CartDirective])
    ;
})();