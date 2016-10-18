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
        .then(response => {
          const results = response.data;
          if (results.success) {
            notifications.show({
              msg: results.success
            });
            // Redirection to the project samples page.
            $window.location.href = this.url;
          }
          if (results['save-errors']) {
            results['save-errors'].forEach(msg => {
              notifications.show({msg, type: 'error'});
            });
          }
        });
    };
  }
};

export default saveMetadata;
