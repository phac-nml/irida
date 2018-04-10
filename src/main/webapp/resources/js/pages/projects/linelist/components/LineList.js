import React from "react";
import Loader from "./Loader";

export const LineList = props => {
  if (props.loading) {
    return <Loader />;
  } else {
    return <h2>Let's create a table ... in the next merge request.</h2>;
  }
};
