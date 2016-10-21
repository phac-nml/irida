const defaults = {type: 'text', label: '', template: null};
const selectedTemplates = new Map();

const metadataInput = {
  templateUrl: `templateInput.tmpl.html`,
  controller(TemplateService) {
    this.template = {
      list: [Object.assign({}, defaults)],
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
      if (item.label) {
        this.template.list.splice(index + 1, 0, Object.assign({}, defaults));
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
        TemplateService
          .getFieldsForTemplates(differentTemplate)
          .then(data => {
            const fields = data.fields;
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

          if (this.template.list.length === 0) {
            this.template.list = [Object.assign({}, defaults)];
          }
        };

        values.forEach(value => removeFromList(value));
      }
    };

    this.removeField = index => {
      this.template.list.splice(index, 1);
    };

    this.saveTemplate = () => {
      // remove any empty fields
      const fields = this.template.list
        .filter(field => field.label.length !== 0);
    };
  }
};

export default metadataInput;
