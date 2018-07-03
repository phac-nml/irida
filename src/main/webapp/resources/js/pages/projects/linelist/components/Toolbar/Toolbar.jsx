import React from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";
import { AddSamplesToCartButton } from "../AddToCartButton/AddSamplesToCart";
import { Button, Icon } from "antd";

const { i18n, urls } = window.PAGE;

export function Toolbar(props) {
  return (
    <div style={{
      marginBottom: ".8rem",
      display: "flex",
      justifyContent: "space-between"
    }}>
      <div style={{ display: "inline-block" }}>
        <ExportDropDown csv={props.exportCSV} excel={props.exportXLSX}/>
        <AddSamplesToCartButton selectedCount={props.selectedCount}
                                addSamplesToCart={props.addSamplesToCart}/>
      </div>
      <div style={{ display: "inline-block" }}>
        <Button href={urls.import}><Icon
          type="cloud-upload"/>{i18n.linelist.importBtn.text}</Button>
      </div>
    </div>
  );
}

Toolbar.propTypes = {
  exportCSV: PropTypes.func.isRequired,
  exportXLSX: PropTypes.func.isRequired,
  addSamplesToCart: PropTypes.func.isRequired
};
