/*
 * Component to show a loading symbol with optional text
 * when data for a page is loading.
 */

import React from "react";
import { Spin } from "antd";
import PropTypes from "prop-types";
import { SPACE_SM } from "../../styles/spacing";

/**
 * Stateless UI component for displaying a loading symbol with optional text
 * @param {string} message - Text to display next to loading symbol
 *
 * @returns {Element} - Returns a 'Spin' component from antd with optional text
 */

export function ContentLoading({ message = "Loading", ...props }) {
  return (
    <span>
      <Spin {...props} style={{ marginRight: SPACE_SM }} />
      {message}
    </span>
  );
}

ContentLoading.propTypes = {
  /*Text to display next to loading symbol*/
  message: PropTypes.string
};
