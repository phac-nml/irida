import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

/**
 * Component to render an [antd Alert]{@link https://ant.design/components/alert/}
 * specifically for displaying information with a consistent icon.
 */
export function InfoAlert(props) {
  return (
    <Alert
      type="info"
      showIcon
      iconType="info-circle-o"
      message={props.message}
      description={props.description}
    />
  );
}

InfoAlert.propTypes = {
  message: PropTypes.string.isRequired,
  description: PropTypes.string
};
