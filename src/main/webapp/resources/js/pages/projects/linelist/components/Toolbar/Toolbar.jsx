import React from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";
import { AddSamplesToCartButton } from "../AddToCartButton/AddSamplesToCart";
import { Button, Input } from "antd";

const { Search } = Input;

const { i18n, urls } = window.PAGE;

export function Toolbar(props) {
  return (
    <div
      style={{
        marginBottom: ".8rem",
        display: "flex",
        justifyContent: "space-between"
      }}
    >
      <div style={{ display: "inline-block" }}>
        <ExportDropDown csv={props.exportCSV} excel={props.exportXLSX}/>
        <AddSamplesToCartButton
          selectedCount={props.selectedCount}
          addSamplesToCart={props.addSamplesToCart}
        />
      </div>
      <div
        className="ant-form ant-form-inline"
        style={{ display: "inline-block" }}
      >
        <Button href={urls.import}>
          <i
            className="fas fa-cloud-upload-alt spaced-right__sm"
            aria-hidden="true"
          />
          {i18n.linelist.importBtn.text}
        </Button>
        <Search
          onKeyUp={e => props.quickSearch(e.target.value)}
          id="js-table-filter"
          className="table-filter t-table-filter"
          style={{
            width: 200,
            marginLeft: ".8rem"
          }}
        />
      </div>
    </div>
  );
}

Toolbar.propTypes = {
  selectedCount: PropTypes.number.isRequired,
  exportCSV: PropTypes.func.isRequired,
  exportXLSX: PropTypes.func.isRequired,
  addSamplesToCart: PropTypes.func.isRequired,
  quickSearch: PropTypes.func.isRequired
};
