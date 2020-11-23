const propertiesReader = require("properties-reader");

const properties = propertiesReader("../resources/configuration.properties");

const defaults = {
  "primary-color": "#1890ff",
  "info-color": "#1890ff",
  "link-color": "#1890ff",
  "font-size-base": "14px",
  "border-radius-base": "2px",
};

function formatAntStyles() {
  const custom = {};
  const re = /styles.ant.([\w+-]*)/;
  properties.each((key, value) => {
    const found = key.match(re);
    if (found) {
      custom[found[1]] = value;
    }
  });
  return Object.assign({}, defaults, custom);
}

module.exports = { formatAntStyles };
