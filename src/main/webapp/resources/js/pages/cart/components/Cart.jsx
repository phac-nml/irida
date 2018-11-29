import React from "react";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { Row, Col, Icon, List, Skeleton, Tag } from "antd";
import { getSampleInfo } from "../../../apis/cart/cart";

class SampleRenderer extends React.Component {
  constructor(props) {
    super(props);
    this.state = props.data;
  }

  componentDidMount() {
    const { node } = this.props;
    const data = {};
    Object.assign(data, this.state);
    if (!data.loaded) {
      getSampleInfo(data.projectId, data.id).then(response => {
        Object.assign(data, response);
        data.loaded = true;
        node.setData(data);
        this.setState(data);
      });
    }
  }

  render() {
    const { loaded } = this.state;

    return (
      <Row type="flex" align="middle">
        <Col span={2}>
          {!loaded ? (
            <Icon type="loading" />
          ) : (
            <Icon
              style={{ fontSize: 20 }}
              type="check-circle"
              theme="twoTone"
              twoToneColor="#52c41a"
            />
          )}
        </Col>
        <Col span={22}>
          <Skeleton paragraph={false} loading={!loaded} active>
            {loaded ? (
              <List.Item actions={[<a>Remove</a>]}>
                <List.Item.Meta
                  title={this.state.label}
                  description={
                    <Tag color="magenta">{this.state.project.label}</Tag>
                  }
                />
              </List.Item>
            ) : null}
          </Skeleton>
        </Col>
      </Row>
    );
  }
}

export default class Cart extends React.Component {
  static propTypes = {
    total: PropTypes.number.isRequired
  };

  columnDefs = [
    {
      headerName: "",
      field: "label",
      cellRenderer: "SampleRenderer"
    }
  ];

  state = {
    samples: []
  };

  componentDidMount() {
    const samples = this.props.ids.map(ids => ({
      id: ids.sampleId,
      projectId: ids.projectId,
      loaded: false
    }));
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
      <div
        className="ag-theme-balham"
        style={{ width: "100%", height: "100%" }}
      >
        <AgGridReact
          headerHeight={0}
          columnDefs={this.columnDefs}
          rowData={this.state.samples}
          frameworkComponents={{ SampleRenderer }}
          onGridReady={this.onGridReady}
          rowHeight={80}
          enableFilter={true}
        />
      </div>
    );
  }
}
