const emptyField = {type: 'text', label: ''}; // Empty template for a new field.
const list = [Object.assign({}, emptyField)]; // Start the list of with an empty item.

export const TemplateInputComponent = {
  bindings: {
    redirecturl: '@', // url to redirect the user after saving the template
    saveurl: '@'      // url to save the template to.
  },
  templateUrl: `templateInput.tmpl.html`,
  controller(TemplateInputService) {
    this.$onInit = () => {
      this.template = {
        list,    // List of fields in the new template
        name: '' // Name of the new template
      };
    };

    /**
     * Button click handler for adding a new field at a specific index.
     * Adds a new empty field below the button.
     * @param {number} index index of the button being clicked.
     */
    this.addField = () => {
      this.template.list
        .push(Object.assign({}, emptyField));
    };

    /**
     * Button click handler for removing a field.
     * @param {number} index index of the field to remove
     */
    this.removeField = index => {
      this.template.list.splice(index, 1);
    };

    /**
     * Button click handler for saving the template.
     */
    this.saveTemplate = () => {
      const fields = this.template.list
        // Remove empty fields
        .filter(field => field.label.length !== 0)
        // Get only the necessary fields.
        .map(field => {
          return ({label: field.label, type: field.type});
        });

      // Call the service to save this template
      TemplateInputService
        .saveTemplate(
          this.saveurl,
          {fields, name: this.template.name},
          this.redirecturl);
    };
  }
};
