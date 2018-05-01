import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import LoadingOverlay from "../../../../modules/agGrid/LoadingOverlay";

const localeText = window.PAGE.i18n.agGrid;

export const Table = props => {
  const { fields } = props;

  const containerStyle = {
    boxSizing: "border-box",
    height: 600,
    width: "100%"
  };

  const frameworkComponents = { LoadingOverlay };

  return (
    <div style={containerStyle} className="ag-theme-balham">
      <AgGridReact
        localeText={localeText}
        columnDefs={fields}
        deltaRowDataMode={true}
        frameworkComponents={frameworkComponents}
        loadingOverlayComponent="LoadingOverlay"
      />
    </div>
  );
};

Table.propTypes = {
  fields: PropTypes.array.isRequired,
  entries: PropTypes.array
};

const mapStateToProps = state => ({
  fields: state.fields.fields
});
const mapDispatchToProps = dispatch => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Table);
