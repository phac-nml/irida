const propertiesReader = require("properties-reader");
const fs = require("fs");

const defaults = {
  "primary-color": "#1890ff",
  "info-color": "#1890ff",
  "link-color": "#1890ff",
  "font-size-base": "2px",
  "border-radius-base": "2px",
};
const iridaConfig = "/etc/irida/irida.conf";
const propertiesConfig =
  "../resources/ca/corefacility/bioinformatics/irida/config/styles.properties";

function formatAntStyles() {
  const colourProperties = {};
  const re = /styles.ant.([\w+-]*)/;

  try {
    if (fs.existsSync(propertiesConfig)) {
      const properties = propertiesReader(propertiesConfig);
      properties.each((key, value) => {
        const found = key.match(re);
        if (found) {
          colourProperties[found[1]] = value;
        }
      });
    } else if (fs.existsSync(iridaConfig)) {
      const properties = propertiesReader(iridaConfig);
      properties.each((key, value) => {
        const found = key.match(re);
        if (found) {
          colourProperties[found[1]] = value;
        }
      });
    }
  } catch (e) {
    console.log("No styles in `/etc/irida/irida.conf`");
  }
  return Object.assign({}, defaults, colourProperties);
}

module.exports = { formatAntStyles };
