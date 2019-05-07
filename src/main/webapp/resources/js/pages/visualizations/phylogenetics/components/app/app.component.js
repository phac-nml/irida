import { METADATA, TEMPLATES, TREE } from "./../../constants";

class AppController {
  constructor($scope) {
    this.loading = true;

    $scope.$on(METADATA.ERROR, () => {
      this.metadataError = true;
    });

    $scope.$on(METADATA.EMPTY, () => {
      this.metadataEmpty = true;
      this.showEmptyMetadataWarning = true;
    });

    $scope.$on(METADATA.LOADED, () => {
      this.metadataLoaded = true;
    });

    $scope.$on(TREE.NOT_LOADED, () => {
      this.treeError = true;
    });

    $scope.$on(TREE.COMPLETED, () => {
      this.loading = false;
    });

    $scope.$on(TEMPLATES.ERROR, () => {
      this.templateError = true;
      this.loading = false;
    });

    this.closeEmptyMetadataWarning = () => {
      this.showEmptyMetadataWarning = false;
    };
  }
}

AppController.$inject = ["$scope"];

export const AppComponent = {
  transclude: true,
  controller: AppController,
  templateUrl: "app-component.tmpl.html"
};
