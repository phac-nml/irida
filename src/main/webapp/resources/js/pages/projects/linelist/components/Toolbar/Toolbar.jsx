import React from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";

export function Toolbar(props) {
  return (
    <div style={{ marginBottom: ".8rem" }}>
      <ExportDropDown csv={props.exportCSV} excel={props.exportXLSX} />
    </div>
  );
}

Toolbar.propTypes = {
  exportCSV: PropTypes.func.isRequired,
  exportXLSX: PropTypes.func.isRequired
};
