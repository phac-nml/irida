const defaults = {type: 'text'};

const metadataInput = {
  templateUrl: `templateInput.tmpl.html`,
  controller() {
    this.list = [Object.assign({}, defaults)];

    this.addField = index => {
      if (this.list[index].label.length > 2) {
        this.list.push(Object.assign({}, defaults));
      }
    };

    this.getUpdatedTemplates = () => {

    };

    this.removeField = index => {
      this.list.splice(index, 1);
    };

    this.saveTemplate = () => {
      console.log(this.list);
    };
  }
};

export default metadataInput;
