const emptyField = {type: 'text', label: ''}; // Empty template for a new field.
const list = [Object.assign({}, emptyField)]; // Start the list of with an empty item.

export const TemplateInputComponent = {
  bindings: {
    redirecturl: '@', // url to redirect the user after saving the template
    templateurl: '@', // url to get the fields of an existing template
    saveurl: '@'      // url to save the template to.
  },
  templateUrl: `templateInput.tmpl.html`,
  controller(TemplateInputService) {
    this.templates = window.PAGE.templates;
    this.templateMap = {};
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

    const getExistingTemplateFields = () => {
      // Reset the map of field in template, this allows the UI to display
      // which template a specific field came from.
      this.templateMap = {};

      let allFields = [];
      for (let template of this.existing) {
        // Make a copy of the fields on this template to prevent overwriting the originals.
        const fields = Array.from(template.fields);

        // Add the fields if they are required
        for (let field of fields) {
          // look this up in the angular docs
          if (!this.templateMap[field.id]) {
            this.templateMap[field.id] = [];
          }
          this.templateMap[field.id].push(template.label);

          // Check to see if the field is already in the list.
          const index = allFields.findIndex(f => f.id === field.id);
          if (index === -1) {
            allFields.push(field);
          }
        }
      }
      return allFields;
    };

    const updateTemplateFields = () => {
      let templateFields = getExistingTemplateFields();
      const fields = Array.from(this.template.list);
      const updatedFields = [];
      for (let field of fields) {
        // See if the a template field has the same label as an existing field.
        // The template label gets priority since it is already an existing
        // metadata field.
        const STD_FIELD_NAME = field.label.toLowerCase();
        const templateFieldMatched = [];
        const templateFieldUnMatched = [];

        for (let tf of templateFields) {
          if (tf.label.toLowerCase() === STD_FIELD_NAME) {
            templateFieldMatched.push(tf);
          } else {
            templateFieldUnMatched.push(tf);
          }
        }

        if (templateFieldMatched.length) {
          // Add the first found field to the list.
          updatedFields.push(templateFieldMatched[0]);

          // Only keep the unmatched fields
          templateFields = templateFieldUnMatched;
        } else {
          // Field label not found in the template fields.
          // Keep the field
          updatedFields.push(field);
        }
      }
      // Combine the leftover UI fields with the leftover template fields
      this.template.list = [...updatedFields, ...templateFields];
    };

    this.onRemoveTemplate = () => {
      updateTemplateFields();
    };

    this.onSelectTemplate = $item => {
      // Check to see if we need to get the fields for the selected template
      if ($item.fields) {
        updateTemplateFields();
      } else {
        TemplateInputService
          .getFieldsForTemplate(this.templateurl, $item.id)
          .then(fields => {
            $item.fields = fields;
            updateTemplateFields();
          });
      }
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
