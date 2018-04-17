import React, { Component } from "react";
import { Spin } from "antd";

const { loading } = window.PAGE.i18n.agGrid;
export default class LoadingOverlay extends Component {
  render() {
    if (typeof loading === "undefined") {
      throw new Error(
        "Expected 'window.PAGE.i18n.agGrid.loading' to be present on the page."
      );
    }
    return (
      <div className="ag-overlay-loading-center irida-ag-overlay">
        <div>
          <Spin style={{ marginRight: "1em" }} />
          <span className="irida-ag-overlay-loading-text">{`${loading}`}</span>
        </div>
      </div>
    );
  }
}
