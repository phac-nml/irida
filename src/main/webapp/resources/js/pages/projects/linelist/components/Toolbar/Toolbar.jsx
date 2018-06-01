import React from "react";
import PropTypes from "prop-types";
import { Button } from "antd";

const { i18n } = window.PAGE;

export function Toolbar(props) {
  return (
    <div style={{ marginBottom: ".8rem" }}>
      <Button onClick={props.exportCSV}>{i18n.linelist.toolbar.export}</Button>
    </div>
  );
}

Toolbar.propTypes = {
  exportCSV: PropTypes.func.isRequired
};
