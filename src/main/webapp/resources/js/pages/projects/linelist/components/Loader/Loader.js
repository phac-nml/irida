import React from "react";
import { Alert } from "antd";

const { message, description } = window.PAGE.i18n.Loader;
export const Loader = () => (
  <Alert
    message={message}
    description={description}
    type="info"
    iconType="loading"
    showIcon
  />
);
