import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { Badge, Button, Col, Icon, Input, Row, Tooltip } from "antd";
import styled from "styled-components";
import { cartPageActions } from "../reducer";
import { sampleDetailsActions } from "../../../components/SampleDetails/reducer";
import { spacing } from "../../../styles";

const { Search } = Input;

const CartSample = styled(Row)`
  width: 375px;
  border-radius: 5px;
  margin: 5px;
  padding: 10px;
  transition: background-color 0.1s ease-in-out;

  &:hover {
    background-color: hsl(210, 9%, 96%);
  }
`;

const CartSamplesWrapper = styled.div`
  display: flex;
  flex-direction: column;
  width: 400px;
  height: 100%;
  overflow-x: hidden;

  /* Hide default table styles to allow for custom layout */
  .ag-root.ag-font-style.ag-layout-normal,
  .ag-row,
  .ag-header.ag-pivot-off {
    border: none !important;
  }

  .ag-cell {
    padding: 0 !important;
  }

  .ag-cell.ag-cell-focus {
    border: none !important;
  }

  .ag-row.ag-row-hover {
    background-color: transparent !important;
  }
`;

const CartTools = styled(Row)`
  padding: ${spacing.DEFAULT};

  .ant-input {
    border: none;
    background-color: hsl(210, 9%, 96%);
  }
`;

class SampleRenderer extends React.Component {
  state = { details: false, filter: "" };

  render() {
    const sample = this.props.data;
    return (
      <CartSample type="flex" align="top" justify="space-between">
        <Col>
          <a href="#" onClick={sample.displayFn}>
            <Badge
              status="success"
              style={{ fontSize: 18 }}
              text={sample.label}
            />
          </a>
          <div style={{ color: "#b0bec4", marginLeft: 15 }}>
            {sample.project.label}
          </div>
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
      </CartSample>
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
      width: 360,
      cellStyle: {
        padding: 0,
        backgroundColor: "transparent"
      }
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
      <CartSamplesWrapper>
        <CartTools type="flex" justify="space-between">
          <Col style={{ width: 290 }}>
            <Search onChange={this.onSearch} value={this.state.filter} />
          </Col>
          <Col>
            <Button onClick={this.props.emptyCart}>Empty</Button>
          </Col>
        </CartTools>
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
      </CartSamplesWrapper>
    );
  }
}

const mapStateToProps = state => ({
  count: state.cart.count,
  samples: state.cartPageReducer.samples
});

const mapDispatchToProps = dispatch => ({
  displaySample: sample => dispatch(sampleDetailsActions.displaySample(sample)),
  emptyCart: () => dispatch(cartPageActions.emptyCart())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);
