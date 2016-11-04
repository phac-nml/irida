const empty = {type: 'text', label: ''};
const list = [Object.assign({}, empty)];
const selectedTemplates = new Map();

export const TemplateInputComponent = {
  bindings: {
    redirecturl: '@'
  },
  templateUrl: `templateInput.tmpl.html`,
  controller(TemplateInputService) {
    this.template = {
      list,
      name: ''
    };

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

      // Check to make sure there is not an empty field
      if (!this.template.list[this.template.list.length - 1].label) {
        this.template.list.pop();
      }

      selectedTemplates.get(template).forEach(field => checkField(field));
    };

    this.addField = index => {
      const item = this.template.list[index];
      const INDEX_WITH_OFFSET = index + 3;
      if (item.label) {
        this.template.list
          .splice(INDEX_WITH_OFFSET, 0, Object.assign({}, empty));
      }
    };

    this.getUpdatedTemplates = () => {
      const wantedTemplates = Array.from(this.existing);
      const currentTemplates = [...selectedTemplates.keys()];

      // See if we added or removed templates
      if (wantedTemplates.length > currentTemplates.length) {
        // Added a template
        // Need to find out which template was added.
        const differentTemplate = wantedTemplates
          .filter(name => currentTemplates.indexOf(name) === -1)[0];

        // Get the new fields
        TemplateInputService
          .getFieldsForTemplates(differentTemplate)
          .then(data => {
            // First two fields will alway be identifier and label
            const fields = data.fields.slice(2);
            selectedTemplates.set(differentTemplate, fields);
            updateFields(differentTemplate);
          });
      } else {
        const differentTemplate = currentTemplates
          .filter(name => wantedTemplates.indexOf(name) === -1)[0];
        const values = selectedTemplates.get(differentTemplate);
        selectedTemplates.delete(differentTemplate);

        const removeFromList = field => {
          const index = this.template.list
            .findIndex(item => item.label === field.label);
          const item = this.template.list[index];
          if (item && Object.keys(item.templates).length > 1) {
            delete item.templates[differentTemplate];
          } else if (item) {
            this.template.list.splice(index, 1);
          }
        };

        values.forEach(value => removeFromList(value));
      }
    };

    this.removeField = index => {
      const newIndex = index + 2; // Offset for the two required fields.
      this.template.list.splice(newIndex, 1);
    };

    this.saveTemplate = () => {
      // remove any empty fields
      const fields = this.template.list
      // Remove empty fields
        .filter(field => field.label.length !== 0);

      // Call the service to save this template
      TemplateInputService
        .saveTemplate({fields, name: this.template.name}, this.redirecturl);
    };
  }
};
