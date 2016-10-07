/**
 * @file AngularJS component for handling saving valid metadata to the server.
 */
const template = `
<button class="btn btn-success pull-right" ng-click="$ctrl.saveMetadata()">
  <span class="fa fa-floppy-o" aria-hidden="true"></span>&nbsp;
  {{ $ctrl.label }}
</button>
`;
const saveMetadata = {
  bindings: {
    url: '@',
    label: '@'
  },
  template,
  controller($window, sampleMetadataService, notifications) {
    this.saveMetadata = () => {
      sampleMetadataService
        .saveMetadata()
        .then(results => {
          console.log(results);
          notifications.show({
            msg: 'Message still needs to be updated.'
          });
          // Redirection to the project samples page.
          $window.location.href = this.url;
        });
    };
  }
};

export default saveMetadata;
