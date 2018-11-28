import React from "react";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { Skeleton } from "antd";

class SampleRenderer extends React.Component {
  constructor(props) {
    super(props);
    this.state = props.data;
  }

  componentDidMount() {
    this.setState({ mounted: true });
  }

  render() {
    return (
      <Skeleton paragraph={false} loading={this.state.loading} active>
        {this.state.label}
      </Skeleton>
    );
  }
}

export default class Cart extends React.Component {
  static propTypes = {
    total: PropTypes.number.isRequired
  };

  columnDefs = [
    {
      headerName: "asdfas",
      field: "label",
      cellRenderer: "SampleRenderer"
    }
  ];

  state = {
    samples: []
  };

  componentDidMount() {
    const samples = this.props.ids.map(id => ({ id, loading: true }));
    this.setState({ samples });
  }

  onGridReady = params => {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;
    params.api.sizeColumnsToFit();
    setTimeout(function() {
      params.api.resetRowHeights();
    }, 500);
  };

  render() {
    return (
      <div className="ag-theme-balham" style={{ width: "100%", height: 800 }}>
        <AgGridReact
          headerHeight={0}
          columnDefs={this.columnDefs}
          rowData={this.state.samples}
          frameworkComponents={{ SampleRenderer }}
          onGridReady={this.onGridReady}
          rowHeight={60}
        />
      </div>
    );
  }
}
