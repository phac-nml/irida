import React, { Fragment } from "react";
import { Loader } from "./Loader";
import { Table } from "./Table";
import { TemplateSelect } from "./TemplateSelect";

export const LineList = props => {
  if (props.loading) {
    // LOADING STATE
    return <Loader />;
  } else if (props.error) {
    // ERROR STATE
    // TODO: (Josh | 2018-04-11) Create error component
    return <h3>A major error has occurred! Better find a bomb shelter!</h3>;
  } else if (props.fields) {
    // FIELDS REQUEST SUCCESSFUL, therefore table can be created
    if (props.fields.length === 0) {
      // EMPTY STATE
      // TODO: (Josh | 2018-04-11) Create Empty state component
      return <h3>Empty state message goes here!</h3>;
    } else {
      // CREATE TABLE
      return (
        <Fragment>
          <div style={{ marginBottom: "1rem" }}>
            <TemplateSelect
              useTemplate={props.useTemplate}
              current={props.current}
              templates={props.templates}
            />
          </div>
          <Table
            fields={props.fields}
            entries={props.entries}
            template={props.template}
          />
        </Fragment>
      );
    }
  }
};
