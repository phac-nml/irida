import React from "react";
import { Alert } from "antd";

const { i18n } = window.PAGE;
export const Loader = () => (
  <Alert
    message={__("linelist.Loader.message")}
    description={
      <div>
        <i className="fas fa-spinner fa-pulse spaced-right__sm" />
        {__("linelist.Loader.description")}
      </div>
    }
    type="info"
  />
);
