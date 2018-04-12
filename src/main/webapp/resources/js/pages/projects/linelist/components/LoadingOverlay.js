import React, { Component } from "react";
import { Icon } from "antd";

const { loading } = window.PAGE.i18n.agGrid;
export default class LoadingOverlay extends Component {
  render() {
    return (
      <div className="ag-overlay-loading-center">
        <div>
          <Icon className="overlay-icon" type="loading" />
          <span className="overlay-text">{`${loading}`}</span>
        </div>
      </div>
    );
  }
}
