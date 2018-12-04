import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { Row, Col, Drawer, Icon, List, Tag } from "antd";

const colors = {};

class SampleRenderer extends React.Component {
  state = { details: false };

  generateColor = id => {
    colors[id] =
      colors[id] ||
      `rgb(${Math.floor(Math.random() * 256)}, ${Math.floor(
        Math.random() * 256
      )}, ${Math.floor(Math.random() * 256)})`;
    return colors[id];
  };

  render() {
    const sample = this.props.data;
    return (
      <Row type="flex" align="middle">
        <Col span={2}>
          <Icon
            style={{ fontSize: 20 }}
            type="check-circle"
            theme="twoTone"
            twoToneColor="#52c41a"
          />
        </Col>
        <Col span={22}>
          <List.Item
            actions={[
              <a>
                <Icon
                  type="info-circle"
                  theme="twoTone"
                  onClick={() => this.setState({ details: true })}
                />
                <Drawer
                  title={sample.label}
                  placement="right"
                  width={500}
                  closable={true}
                  onClose={() => this.setState({ details: false })}
                  visible={this.state.details}
                >
                  <h3>JELLO GOES IN HERE</h3>
                  <p>THere could be a detailed breakdown of the sample including QC, files, etc...</p>
                </Drawer>
              </a>,
              <a onClick={() => console.log(sample)}>
                <Icon type="close" />
              </a>
            ]}
          >
            <List.Item.Meta
              title={sample.label}
              description={
                <Tag color={this.generateColor(sample.project.id)}>
                  {sample.project.label}
                </Tag>
              }
            />
          </List.Item>
        </Col>
      </Row>
    );
  }
}

class CartSamplesComponent extends React.Component {
  static propTypes = {
    count: PropTypes.number.isRequired,
    samples: PropTypes.array.isRequired
  };

  columnDefs = [
    {
      headerName: "",
      field: "label",
      cellRenderer: "SampleRenderer"
    }
  ];

  constructor(props) {
    super(props);
    /*
    To show some default state to the user we fill an empty array with the amount
    of samples in the cart, with a not loaded indication.
     */
    const samples = new Array(props.count);
    this.state = { samples };
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (this.props.samples.length > prevProps.samples.length) {
      this.setState({ samples: this.props.samples });
    }
  }

  onGridReady = params => {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;
    params.api.sizeColumnsToFit();
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

const mapStateToProps = state => ({
  count: state.cart.count,
  samples: state.cartPageReducer.samples
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);
