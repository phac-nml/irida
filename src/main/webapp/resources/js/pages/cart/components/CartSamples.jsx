import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { Button, Col, Input, Row } from "antd";
import styled from "styled-components";
import { cartPageActions } from "../reducer";
import { sampleDetailsActions } from "../../../components/SampleDetails/reducer";
import { CartSampleRenderer } from "./SampleRenderer";
import { COLOURS, SPACING } from "../../../styles";

const { Search } = Input;

const CartSamplesWrapper = styled.div`
  height: 100%;
  width: 100%;
  padding-top: 65px;
`;

const CartTools = styled(Row)`
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  padding: ${SPACING.DEFAULT};
  border-bottom: 2px solid ${COLOURS.LIGHT_GRAY};
  height: 65px;

  .ant-input {
    border: none;
    background-color: hsl(210, 9%, 96%);
  }
`;

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
      cellRenderer: "CartSampleRenderer",
      cellStyle: {
        padding: SPACING.DEFAULT,
        width: "380px"
      }
    }
  ];

  state = { filter: "", samples: null };

  onGridReady = params => {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;
  };

  onSearch = e => {
    const filter = e.target.value;
    this.setState({ filter });
  };

  render() {
    const samples = this.props.samples.filter(s =>
      s.label.includes(this.state.filter)
    );
    return (
      <div
        style={{
          height: "100%",
          position: "relative"
        }}
      >
        <CartTools type="flex" justify="space-between">
          <Col>
            <Search
              style={{ width: 285 }}
              onChange={this.onSearch}
              value={this.state.filter}
            />
          </Col>
          <Col>
            <Button onClick={this.props.emptyCart}>Empty</Button>
          </Col>
        </CartTools>
        <CartSamplesWrapper className="ag-theme-material">
          <AgGridReact
            headerHeight={0}
            columnDefs={this.columnDefs}
            rowData={samples}
            frameworkComponents={{ CartSampleRenderer }}
            onGridReady={this.onGridReady}
            rowHeight={80}
            filter={true}
          />
        </CartSamplesWrapper>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  count: state.cart.count,
  samples: state.cartPageReducer.samples
});

const mapDispatchToProps = dispatch => ({
  displaySample: sample => dispatch(sampleDetailsActions.displaySample(sample)),
  emptyCart: () => dispatch(cartPageActions.emptyCart()),
  removeSample: id => dispatch(cartPageActions.removeSample(id))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);
