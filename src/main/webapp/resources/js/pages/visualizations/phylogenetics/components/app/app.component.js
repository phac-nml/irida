import {ERRORS} from './../../constants';

function appController($scope) {
  $scope.$on(ERRORS.METADATA, () => {
    this.metadataError = true;
  });
}

appController.$inject = [
  '$scope'
];

export const AppComponent = {
  transclude: true,
  controller: appController,
  templateUrl: 'app-component.tmpl.html'
};
