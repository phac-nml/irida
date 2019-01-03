import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { Badge, Button, Col, Icon, Input, Row, Tooltip } from "antd";
import { actions } from "../reducer";

const { Search } = Input;

class SampleRenderer extends React.Component {
  state = { details: false, filter: "" };

  render() {
    const sample = this.props.data;
    return (
      <Row
        type="flex"
        align="top"
        justify="space-between"
        className="sample-listing"
      >
        <Col>
          <a href="#" onClick={sample.displayFn}>
            <Badge
              status="success"
              style={{ fontSize: 18 }}
              text={sample.label}
            />
          </a>
          <div style={{ color: "#b0bec4", marginLeft: 15 }}>{sample.project.label}</div>
        </Col>
        <Col>
          <Tooltip title="Remove from cart">
            <Button
              ghost
              shape="circle"
              size="small"
              style={{ border: "none" }}
            >
              <Icon type="close-circle" style={{ color: "#222" }} />
            </Button>
          </Tooltip>
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
      cellRenderer: "SampleRenderer",
      width: 360
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

  onSearch = e => {
    const filter = e.target.value;

    this.setState({ filter }, () => {
      this.setState({
        samples: this.props.samples.filter(s => s.label.includes(filter))
      });
    });
  };

  render() {
    return (
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          width: 400,
          height: "100%",
          overflowX: "hidden"
        }}
      >
        <Row type="flex" justify="space-between" className="sample-search">
          <Col style={{ width: 290 }}>
            <Search onChange={this.onSearch} value={this.state.filter} />
          </Col>
          <Col>
            <Button onClick={this.props.emptyCart}>Empty</Button>
          </Col>
        </Row>
        <div className="ag-theme-balham" style={{ flexGrow: 1 }}>
          <AgGridReact
            headerHeight={0}
            columnDefs={this.columnDefs}
            rowData={this.state.samples}
            frameworkComponents={{ SampleRenderer }}
            onGridReady={this.onGridReady}
            rowHeight={80}
            enableFilter={true}
            suppressHorizontalScroll={true}
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
  displaySample: sample => dispatch(actions.displaySample(sample)),
  emptyCart: () => dispatch(actions.emptyCart())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);
