import {EVENTS} from './../../constants';

const templateUrl = 'metadata.button.tmpl';
const asideTemplateUrl = 'metadata.aside.tmpl';

const FIELDS = window.headersList.map((header, index) => {
  return ({text: header, index, selected: true});
});

export const MetadataComponent = {
  templateUrl,
  controller: class MetadataComponent {
    constructor($aside) {
      this.$aside = $aside;
    }

    showMetadataTemplator() {
      this.$aside.open({
        templateUrl: asideTemplateUrl,
        openedClass: 'metadata-open',
        controllerAs: '$ctrl',
        controller() {
          this.fields = Object.assign(FIELDS);

          this.toggleField = field => {
            // Broadcast change column visibility
            const event = new CustomEvent(
              EVENTS.TABLE.columnVisibility,
              {
                detail: {
                  column: field.index
                },
                bubbles: true
              });
            document.body.querySelector('.modal-content')
              .dispatchEvent(event);
          };
        },
        placement: 'left',
        size: 'sm'
      });
    }
  }
};
