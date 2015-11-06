(function (angular, page) {
  /**
   * Custom Select2 directive for searching through users that on not
   * currently on this project.
   * JQuery Select2 plugin.
   * @returns {{restrict: string, require: string, link: Function}}
   */
  function select2() {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        $(elem).select2({
            minimumInputLength: 2,
            ajax: {
              url: attrs.url,
              dataType: 'json',
              data: function(term) {
                return {
                  term: term,
                  page_limit: 10
                };
              },
              results: function(data) {
                var results = [];

                for (var id in data) {
                  results.push({
                    "id": id,
                    "text": data[id]
                  });
                }

                return {results: results};
              }
            }
          });
      }
    };
  }

  function MembersService ($http, notifications) {
    function addMember(user) {
      $http({
        method: 'POST',
        url   : page.urls.addMember,
        data  : user
      }).then(function (data) {
        console.log(data);
        PAGE.table.ajax.reload();
        notifications.show({'msg': data.data.result});
      }, function () {
        notifications.show({'msg': page.langs.addMember.error, type: 'error'});
      })
    }

    return {
      addMember: addMember
    };
  }

  function MembersController(MembersService, $modal) {
    var vm = this;

    vm.showAddMemberModal = function () {
      $modal.open({
        templateUrl: 'newMemberModal.html',
        controller: 'NewMemberModalController',
        controllerAs: 'modalCtrl'
      }).result.then(function (user) {
        MembersService.addMember(user);
      });
    };
  }

  function NewMemberModalController ($modalInstance) {
    var vm = this;
    vm.user = {projectRole: 'PROJECT_USER'};

    vm.cancel = function () {
      $modalInstance.dismiss();
    };

    vm.addMember = function () {
      $modalInstance.close(vm.user);
    };
  }

  angular.module('irida.project.members', ['ui.bootstrap'])
    .service('MembersService', ['$http', 'notifications', MembersService])
    .directive('select2', [select2])
    .controller('MembersController', ['MembersService', '$modal', MembersController])
    .controller('NewMemberModalController', ['$modalInstance', NewMemberModalController])
  ;
})(window.angular, window.PAGE);