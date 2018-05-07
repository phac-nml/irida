import React from "react";
import { Alert } from "antd";

const { i18n } = window.PAGE;
export const Loader = () => (
  <Alert
    message={i18n.linelist.Loader.message}
    description={i18n.linelist.Loader.description}
    type="info"
    iconType="loading"
    showIcon
  />
);
