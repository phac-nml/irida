import React from "react";

import { Alert } from "antd";
import { LoadingOutlined } from "@ant-design/icons";

export const Loader = () => (
  <Alert
    message={i18n("linelist.Loader.message")}
    description={
      <div>
        <LoadingOutlined />
        {i18n("linelist.Loader.description")}
      </div>
    }
    type="info"
  />
);
