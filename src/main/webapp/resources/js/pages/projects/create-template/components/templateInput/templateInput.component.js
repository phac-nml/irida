const emptyField = {type: 'text', label: ''}; // Empty template for a new field.
const list = [Object.assign({}, emptyField)]; // Start the list of with an empty item.
const selectedTemplates = new Map();          // Map of the current existing templates displayed to the screen
const nameMap = new Map();                    // Map of the names of the existing templates used

/**
 * Add the name of the existing template to the list of selected templates
 * @param {number} id of the added template
 */
const setSelectedTemplateName = id => {
  if (!nameMap.has(id)) {
    const selectInput = document.querySelector('#existing-templates');
    nameMap.set(id, selectInput.options[selectInput.selectedIndex].innerText);
  }
};

export const TemplateInputComponent = {
  bindings: {
    redirecturl: '@', // url to redirect the user after saving the template
    templateurl: '@', // url to get the fields of an existing template
    saveurl: '@'      // url to save the template to.
  },
  templateUrl: `templateInput.tmpl.html`,
  controller(TemplateInputService) {
    this.nameMap = nameMap;
    this.template = {
      list,
      name: ''
    };

    /**
     * Update the new template when an existing template is either added or removed.
     * @param {string} template name to either add or remove
     */
    const updateFields = template => {
      /**
       * Quick check to see if the field is already in the list.
       * @param {object} field from list or undefined
       */
      const checkField = field => {
        const listItem = this.template.list.find(x => x.label === field.label);
        if (typeof listItem === 'undefined') {
          field.templates = {};
          field.templates[template] = true;
          this.template.list.push(field);
        } else {
          listItem.templates[template] = true;
        }
      };

      // Check to make sure there is not an empty field at the end of the list.
      if (!this.template.list[this.template.list.length - 1].label) {
        this.template.list.pop();
      }

      selectedTemplates.get(template).forEach(field => checkField(field));
    };

    /**
     * Button click handler for adding a new field at a specific index.
     * Adds a new empty field below the button.
     * @param {number} index index of the button being clicked.
     */
    this.addField = index => {
      const item = this.template.list[index];
      const INDEX_WITH_OFFSET = index + 3;
      if (item.label) {
        this.template.list
          .splice(INDEX_WITH_OFFSET, 0, Object.assign({}, emptyField));
      }
    };

    /**
     * Handler for adding an existing template.
     */
    this.getUpdatedTemplates = () => {
      const wantedTemplates = Array.from(this.existing);
      const currentTemplates = [...selectedTemplates.keys()];

      // See if we added or removed templates
      if (wantedTemplates.length > currentTemplates.length) {
        // Added a template
        // Need to find out which template was added.
        const differentTemplate = wantedTemplates
          .filter(name => currentTemplates.indexOf(name) === -1)[0];

        // Store the name of the template.
        setSelectedTemplateName(differentTemplate);

        // Get the new fields
        TemplateInputService
          .getFieldsForTemplates(this.templateurl, differentTemplate)
          .then(fields => {
            selectedTemplates.set(differentTemplate, fields);
            updateFields(differentTemplate);
          });
      } else {
        // This area is for removing an existing template that was previously selected
        // Need to make sure we don't remove fields that come from other templates too.
        const templateToRemove = currentTemplates
          .filter(name => wantedTemplates.indexOf(name) === -1)[0];
        const values = selectedTemplates.get(templateToRemove);
        selectedTemplates.delete(templateToRemove);

        const removeFromList = field => {
          const index = this.template.list
            .findIndex(item => item.label === field.label);
          const item = this.template.list[index];
          if (item && Object.keys(item.templates).length > 1) {
            delete item.templates[templateToRemove];
          } else if (item) {
            this.template.list.splice(index, 1);
          }
        };

        values.forEach(value => removeFromList(value));
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
