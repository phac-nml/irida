import React from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";
import { Button, Icon } from "antd";

export function Toolbar(props) {
  return (
    <div style={{ marginBottom: ".8rem" }}>
      <Button
        style={{ marginRight: "4px" }}
        onClick={() => props.addSamplesToCart()}
        icon="shopping-cart"
      />
    <ExportDropDown csv={props.exportCSV} excel={props.exportXLSX} />
</div>
  );
}

Toolbar.propTypes = {
  exportCSV: PropTypes.func.isRequired,
  exportXLSX: PropTypes.func.isRequired,
  addSamplesToCart: PropTypes.func.isRequired
};
