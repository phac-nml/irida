const propertiesReader = require("properties-reader");

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

  try {
    const properties = propertiesReader("/etc/irida/irida.conf");
    properties.each((key, value) => {
      const found = key.match(re);
      if (found) {
        custom[found[1]] = value;
      }
    });
  } catch (e) {
    console.log("No styles in `/etc/irida/irida.conf`");
  }
  return Object.assign({}, defaults, custom);
}

module.exports = { formatAntStyles };
