import React from "react";

import PropTypes from "prop-types";
import { Loader } from "../Loader";
import { LineListLayoutComponent } from "./LineListLayoutComponent";
import { ErrorAlert } from "../../../../../components/alerts/ErrorAlert";

const { project } = window.PAGE;

/**
 * Container class for the higher level states of the page:
 * 1. Loading
 * 2. Table
 * 3. Loading error.
 */
export function LineList(props) {
  const { initializing } = props;
  if (initializing) {
    return <Loader />;
  } else if (props.error) {
    return (
      <ErrorAlert message={i18n("linelist.error.message", project.name)} />
    );
  }

  return <LineListLayoutComponent {...props} />;
}

LineList.propTypes = {
  initializing: PropTypes.bool.isRequired,
  error: PropTypes.bool
};
