import React, { Component } from "react";
import { Spin } from "antd";

const { i18n } = window.PAGE;
export class LoadingOverlay extends Component {
  render() {
    return (
      <div className="ag-overlay-loading-center irida-ag-overlay">
        <div>
          <Spin style={{ marginRight: "1rem" }} />
          <span className="irida-ag-overlay-loading-text">{`${
            __("linelist.agGrid.loading")
          }`}</span>
        </div>
      </div>
    );
  }
}
