const defaults = {type: 'text'};

const metadataInput = {
  templateUrl: `templateInput.tmpl.html`,
  controller() {
    this.list = [];
    this.field = Object.assign({}, defaults);

    this.createField = () => {
      console.log('Called');
      if (this.list.indexOf(this.field) > -1) {
        console.log('this already exists');
      } else if (this.field) {
        this.list.push(this.field);
        this.field = Object.assign({}, defaults);
      }
    };
  }
};

export default metadataInput;
