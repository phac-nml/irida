import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

export function WarningAlert(props) {
  return (
    <Alert
      type="warning"
      showIcon
      message={props.message}
      description={props.description}
    />
  );
}

WarningAlert.propTypes = {
  message: PropTypes.string.isRequired,
  description: PropTypes.string
};
