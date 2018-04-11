import React from "react";
import "ag-grid";
import { AgGridReact, AgGridColumn } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

const localeText = window.PAGE.i18n.agGrid;

const Table = props => {
  let containerStyle = {
    height: 131
  };

  return (
    <div>
      <div style={containerStyle} className="ag-theme-balham">
        <AgGridReact localeText={localeText} rowData={null}>
          {props.fields.map(f => (
            <AgGridColumn key={f.label} field={f.label} />
          ))}
        </AgGridReact>
      </div>
    </div>
  );
};

export default Table;
