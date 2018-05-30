import React, { Component } from "react";
import { Spin } from "antd";

const { i18n } = window.PAGE;
export class LoadingOverlay extends Component {
  render() {
    if (typeof i18n.linelist.agGrid.loading === "undefined") {
      throw new Error(
        "Expected 'window.PAGE.i18n.linelist.agGrid.loading' to be present on the page."
      );
    }
    return (
      <div className="ag-overlay-loading-center irida-ag-overlay">
        <div>
          <Spin style={{ marginRight: "1rem" }} />
          <span className="irida-ag-overlay-loading-text">{`${
            i18n.linelist.agGrid.loading
          }`}</span>
        </div>
      </div>
    );
  }
}
