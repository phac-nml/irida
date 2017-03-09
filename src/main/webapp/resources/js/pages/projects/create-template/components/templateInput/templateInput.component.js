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

    const getSetOfExistingTemplateFields = () => {
      // Reset the map of field in template;
      this.templateMap = {};
      let allFields = new Set();
      for (let template of this.existing) {
        // Make a copy of the fields on this template
        const fields = Array.from(template.fields);
        for (let field of fields) {
          if (!this.templateMap[field.id]) {
            this.templateMap[field.id] = [];
          }
          this.templateMap[field.id].push(template.label);
          allFields.add(field);
        }
      }
      return allFields;
    };

    const updateTemplateFields = () => {
      const templateFieldSet = getSetOfExistingTemplateFields();
      console.log(templateFieldSet);
      const fields = Array.from(this.template.list);
      const updatedFields = [];
      for (let field of fields) {
        // Check to see if the field is in the chosen templates.
        if (templateFieldSet.has(field)) {
          updatedFields.push(field);
          templateFieldSet.delete(field);
        } else if (!field.id) {
          let found = false;
          // if the field does not have an id then it is a new field.
          // Check to see if the label is in the templated fields.
          for (let setField of templateFieldSet) {
            if (setField.label === field.label) {
              updatedFields.push(setField);
              templateFieldSet.delete(setField);
              found = true;
              break;
            }
          }

          // IF the label is not found in the template fields.  Keep the field
          if (!found) {
            updatedFields.push(field);
          }
        }
      }
      // Update the UI with the new list of fields
      this.template.list = [...updatedFields, ...templateFieldSet];
    };

    this.onRemoveTemplate = ($item, $model) => {
      console.log('Removed: ', $item, this.existing);
    };

    this.onSelectTemplate = $item => {
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

      // Check to see if we already have the fields for this template
      // If not get them, and add them to the template.
      // for(let template of this.existing) {

      // }
      // const wantedTemplates = Array.from(this.existing);
      // const currentTemplates = [...selectedTemplates.keys()];
      //
      // // See if we added or removed templates
      // if (wantedTemplates.length > currentTemplates.length) {
      //   // Added a template
      //   // Need to find out which template was added.
      //   const differentTemplate = wantedTemplates
      //     .filter(name => currentTemplates.indexOf(name) === -1)[0];
      //
      //   // Store the name of the template.
      //   setSelectedTemplateName(differentTemplate);
      //
      //   // Get the new fields
      //   TemplateInputService
      //     .getFieldsForTemplates(this.templateurl, differentTemplate)
      //     .then(fields => {
      //       selectedTemplates.set(differentTemplate, fields);
      //       updateFields(differentTemplate);
      //     });
      // } else {
      //   // This area is for removing an existing template that was previously selected
      //   // Need to make sure we don't remove fields that come from other templates too.
      //   const templateToRemove = currentTemplates
      //     .filter(name => wantedTemplates.indexOf(name) === -1)[0];
      //   const values = selectedTemplates.get(templateToRemove);
      //   selectedTemplates.delete(templateToRemove);
      //
      //   const removeFromList = field => {
      //     const index = this.template.list
      //       .findIndex(item => item.label === field.label);
      //     const item = this.template.list[index];
      //     if (item && Object.keys(item.templates).length > 1) {
      //       delete item.templates[templateToRemove];
      //     } else if (item) {
      //       this.template.list.splice(index, 1);
      //     }
      //   };
      //
      //   values.forEach(value => removeFromList(value));
      // }
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
