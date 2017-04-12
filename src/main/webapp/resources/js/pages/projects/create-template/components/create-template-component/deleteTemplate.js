class DeleteTemplateController {
  constructor($uibModalInstance, template) {
    this.modal = $uibModalInstance;
    this.template = template;
  }

  confirm() {
    this.modal.close();
  }

  dismiss() {
    this.modal.dismiss();
  }
}

DeleteTemplateController.$inject = [
  '$uibModalInstance',
  'template'
];

/**
 * Displays a modal to ensure that the user wants to delete the template.
 * @param {object} $uibModal angular-ui modal object.
 * @return {object} promise that the modal will close.
 */
export function deleteTemplate($uibModal) {
  return function(template) {
    const options = {
      templateUrl: `deleteTemplate.tmpl.html`,
      controllerAs: 'modal',
      controller: DeleteTemplateController,
      resolve: {
        template() {
          return template;
        }
      }
    };

    const modal = $uibModal.open(options);

    return modal.result;
  };
}
