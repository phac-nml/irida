(function () {
    "use strict";

    function CartController($scope, cart) {
        "use strict";
        var vm = this;
        vm.show = false;
        vm.projects = [];
        vm.count = 0;
        vm.collapsed = {};

        $scope.$on('cart.add', function () {
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

    function CartDirective() {
        return {
            restrict    : "E",
            templateUrl : "/cart.html",
            replace: true,
            controllerAs: "cart",
            controller  : ['$scope', 'CartService', CartController]
        }
    }

    function CartService(scope, $http) {
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

        svc.add = function (projectId, sampleIds) {
            if (sampleIds.length) {
                $http.post(urls.add, {projectId: projectId, sampleIds: sampleIds})
                  .success(function () {
                      scope.$broadcast("cart.add", {});
                  });
            }
        };

    }

    angular
      .module('irida.cart', [])
      .service('CartService', ['$rootScope', '$http', CartService])
      .directive('cart', [CartDirective])
    ;
})();