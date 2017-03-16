function appController($scope) {
  this.hasErrors = true;
}

appController.$inject = [
  '$scope'
];

export const AppComponent = {
  transclude: true,
  controller: appController,
  templateUrl: 'app-component.tmpl.html'
};
