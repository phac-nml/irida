import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

export function ErrorAlert(props) {
  return (
    <Alert
      message={props.message}
      description={props.description}
      type="error"
      showIcon
    />
  );
}

ErrorAlert.propTypes = {
  message: PropTypes.string.isRequired,
  description: PropTypes.string
};
