import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import LoadingOverlay from "../../../../../../modules/agGrid/LoadingOverlay";

const { i18n } = window.PAGE;

export const Table = props => {
  const containerStyle = {
    boxSizing: "border-box",
    height: 600,
    width: "100%"
  };

  const frameworkComponents = { LoadingOverlay };

  // Need to convert the immutable object here.
  const fields = props.fields.toJSON()[0];

  return (
    <div style={containerStyle} className="ag-theme-balham">
      <AgGridReact
        localeText={i18n.linelist.agGrid}
        columnDefs={fields}
        deltaRowDataMode={true}
        frameworkComponents={frameworkComponents}
        loadingOverlayComponent="LoadingOverlay"
      />
    </div>
  );
};

Table.propTypes = {
  fields: ImmutablePropTypes.list.isRequired,
  entries: PropTypes.array
};
