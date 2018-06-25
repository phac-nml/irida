import React from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";
import { AddSamplesToCartButton } from "../AddToCartButton/AddSamplesToCart";

export function Toolbar(props) {
  return (
    <div style={{ marginBottom: ".8rem" }}>
      <ExportDropDown csv={props.exportCSV} excel={props.exportXLSX} />
      <AddSamplesToCartButton selectedCount={props.selectedCount} addSamplesToCart={props.addSamplesToCart} />
    </div>
  );
}

Toolbar.propTypes = {
  exportCSV: PropTypes.func.isRequired,
  exportXLSX: PropTypes.func.isRequired,
  addSamplesToCart: PropTypes.func.isRequired
};
