import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { Button, Col, Icon, Input, List, Row, Tag } from "antd";
import { actions } from "../reducer";

const {Search} = Input;

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
              <Button
                icon="info"
                shape="circle"
                size="small"
                onClick={sample.displayFn}
              />,
              <Button
                shape="circle"
                icon="close"
                size="small"
                onClick={() =>
                  console.info("Handle removing a sample from the cart", sample)
                }
              />
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
    samples: PropTypes.array.isRequired,
    displaySample: PropTypes.func.isRequired
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
      const samples = this.props.samples.map(s => ({
        ...s,
        displayFn: () => {
          this.props.displaySample(s);
        }
      }));
      this.setState({ samples });
    }
  }

  onGridReady = params => {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;
    params.api.sizeColumnsToFit();
  };

  render() {
    return (
      <div style={{ display: "flex", flexDirection: "column", width: 400, height: "100%", overflowX: "hidden" }}>
        <div className="sample-search">
          <Search />
        </div>
        <div className="ag-theme-balham" style={{ flexGrow: 1 }}>
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
      </div>
    );
  }
}

const mapStateToProps = state => ({
  count: state.cart.count,
  samples: state.cartPageReducer.samples
});

const mapDispatchToProps = dispatch => ({
  displaySample: sample => dispatch(actions.displaySample(sample))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);
