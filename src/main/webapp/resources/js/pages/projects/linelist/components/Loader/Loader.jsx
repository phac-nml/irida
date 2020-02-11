import React from "react";

import { Alert } from "antd";

export const Loader = () => (
  <Alert
    message={i18n("linelist.Loader.message")}
    description={
      <div>
        <i className="fas fa-spinner fa-pulse spaced-right__sm" />
        {i18n("linelist.Loader.description")}
      </div>
    }
    type="info"
  />
);
