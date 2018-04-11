import React from "react";
import Loader from "./Loader";
import Table from "./Table";

export const LineList = props => {
  if (props.loading) {
    return <Loader />;
  } else {
    return <Table fields={props.fields} entries={props.entries} />;
  }
};
