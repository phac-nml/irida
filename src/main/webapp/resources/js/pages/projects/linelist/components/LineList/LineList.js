import React from "react";
import PropTypes from "prop-types";
import { Loader } from "../Loader/Loader";

/**
 * Container class for the higher level states of the page:
 * 1. Loading
 * 2. Table
 * 3. Loading error.
 */
export const LineList = props => {
  const { initializing } = props;
  if (initializing) {
    return <Loader />;
  }
  return (
    <h3>
      Are You Ready to Make Some Tables?{" "}
      <small>Well, you need to wait until the next merge request 😎</small>
    </h3>
  );
};

LineList.propTypes = {
  initializing: PropTypes.bool.isRequired
};
