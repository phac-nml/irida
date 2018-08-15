import React from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";
import { AddSamplesToCartButton } from "../AddToCartButton/AddSamplesToCart";
import { Button, Form, Input } from "antd";

const { Search } = Input;

const { i18n, urls } = window.PAGE;

export function Toolbar(props) {
  return (
    <div className="toolbar">
      <div className="toolbar-group">
        <Form layout="inline">
          <Form.Item>
            <ExportDropDown csv={props.exportCSV} excel={props.exportXLSX} />
          </Form.Item>
          <Form.Item>
            <AddSamplesToCartButton
              selectedCount={props.selectedCount}
              addSamplesToCart={props.addSamplesToCart}
            />
          </Form.Item>
        </Form>
      </div>
      <div className="toolbar-group">
        <Form layout="inline">
          <Form.Item>
            <Button href={urls.import}>
              <i
                className="fas fa-cloud-upload-alt spaced-right__sm"
                aria-hidden="true"
              />
              {i18n.linelist.importBtn.text}
            </Button>
          </Form.Item>
          <Form.Item>
            <Search
              onKeyUp={e => props.quickSearch(e.target.value)}
              id="js-table-filter"
              className="table-filter t-table-filter"
              style={{
                width: 200
              }}
            />
          </Form.Item>
        </Form>
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
