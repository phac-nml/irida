import {EVENTS} from './../../constants';

const templateUrl = 'metadata.button.tmpl';
const asideTemplateUrl = 'metadata.aside.tmpl';

export const MetadataComponent = {
  templateUrl,
  controller: class MetadataComponent {
    constructor($aside) {
      this.$aside = $aside;
    }

    showMetadataTemplator() {
      this.$aside.open({
        templateUrl: asideTemplateUrl,
        controllerAs: '$ctrl',
        controller() {
          this.fields = window.headersList.map((header, index) => {
            return ({text: header, index, selected: true});
          });

          this.toggleField = (e, field) => {
            // Broadcast change column visibility
            const event = new CustomEvent(
              EVENTS.TABLE.columnVisibility,
              {
                detail: {
                  column: field.index
                },
                bubbles: true
              });
            e.currentTarget
              .dispatchEvent(event);
          };
        },
        placement: 'left',
        size: 'sm'
      });
    }
  }
};
