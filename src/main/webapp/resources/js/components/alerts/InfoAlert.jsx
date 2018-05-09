import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

export function InfoAlert(props) {
  return (
    <Alert
      type="info"
      showIcon
      iconType="info-circle-o"
      message={props.message}
    />
  );
}

InfoAlert.propTypes = {
  message: PropTypes.string.isRequired
};
