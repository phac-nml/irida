import {ERRORS, METADATA} from './../../constants';

class AppController {
  constructor($scope) {
    $scope.$on(ERRORS.METADATA, () => {
      this.metadataError = true;
    });

    $scope.$on(ERRORS.TREE_NOT_LOADED, () => {
      this.treeError = true;
    });

    $scope.$on(METADATA.LOADED, () => {
      this.metadataLoaded = true;
    });
  }
}

AppController.$inject = [
  '$scope'
];

export const AppComponent = {
  transclude: true,
  controller: AppController,
  templateUrl: 'app-component.tmpl.html'
};
