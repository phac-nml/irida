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

    this.removeField = index => {
      this.list.splice(index, 1);
    };
  }
};

export default metadataInput;
