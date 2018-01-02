import { showNotification } from "../../../../modules/notifications";

/**
 * @file AngularJS component for handling saving valid metadata to the server.
 */
const template = `
<span class="pull-right"> 
  <button ng-hide="$ctrl.saving" 
          class="btn btn-success" 
          ng-click="$ctrl.saveMetadata()">
    <i class="fa fa-floppy-o" aria-hidden="true"></i>&nbsp;
    {{ $ctrl.label }}
  </button>
  <button ng-show="$ctrl.saving"
          disabled="disabled"
          class="btn btn-success">
     <i class="fa fa-circle-o-notch fa-spin fa-fw" aria-hidden="true"></i>&nbsp;
</button>
</span>
`;
const saveMetadata = {
  bindings: {
    url: "@",
    label: "@"
  },
  template,
  controller: [
    "$window",
    "sampleMetadataService",
    function($window, sampleMetadataService) {
      this.saveMetadata = () => {
        this.saving = true;
        sampleMetadataService.saveMetadata().then(response => {
          const results = response.data;
          if (results.success) {
            showNotification({
              text: results.success
            });
            $window.location.href = this.url;
          }
          if (results["save-errors"]) {
            results["save-errors"].forEach(text => {
              showNotification({ text, type: "error" });
            });
          }
          this.saving = false;
        });
      };
    }
  ]
};

export default saveMetadata;
