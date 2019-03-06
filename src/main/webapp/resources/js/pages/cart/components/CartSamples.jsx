import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { Button, Col, Input, Row } from "antd";
import styled from "styled-components";
import { actions } from "../../../redux/reducers/cart";
import { sampleDetailsActions } from "../../../components/SampleDetails/reducer";
import { SampleRenderer } from "./SampleRenderer";
import { getCartIds, getSamplesForProject } from "../../../apis/cart/cart";
import { COLOR_BACKGROUND_LIGHT, grey4 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";

const { Search } = Input;

const CartSamplesWrapper = styled.div`
  height: 100%;
  width: 100%;
  padding-top: 65px;

  .ag-root {
    border: none !important;
  }

  .ag-center-cols-container {
    width: 100% !important;
  }
`;

const CartTools = styled(Row)`
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  padding: ${SPACE_MD};
  height: 65px;
  border-bottom: 1px solid ${grey4};

  .ant-input {
    border: none;
    background-color: ${COLOR_BACKGROUND_LIGHT};
  }
`;

class CartSamplesComponent extends React.Component {
  static propTypes = {
    count: PropTypes.number.isRequired,
    displaySample: PropTypes.func.isRequired,
    removeSample: PropTypes.func.isRequired,
    removeProject: PropTypes.func.isRequired
  };

  columnDefs = [
    {
      headerName: "",
      field: "label",
      cellRenderer: "SampleRenderer",
      cellStyle: {
        padding: SPACE_MD,
        width: "100%"
      }
    }
  ];

  state = { filter: "", samples: [] };

  onGridReady = params => {
    this.gridApi = params.api;
    this.columnApi = params.columnApi;

    // Add methods for handling the sample
    this.gridApi.displaySample = this.props.displaySample;
    this.gridApi.removeSample = this.removeSample;
    this.gridApi.removeProject = this.removeProject;

    // Fetch the samples, since no samples will be added we do not need redux for them.
    getCartIds().then(({ ids }) =>
      ids.forEach(id => {
        getSamplesForProject(id).then(samples =>
          this.setState(prevState => ({
            samples: [...prevState.samples, ...samples]
          }))
        );
      })
    );
  };

  removeSample = (index, sample) => {
    this.props.removeSample(sample.project.id, sample.id);
    const row = this.gridApi.getRowNode(sample.id);
    this.gridApi.updateRowData({ remove: [row] });
  };

  removeProject = id => {
    this.props.removeProject(id);
    const rows = [];
    this.gridApi.forEachNode((node, index) => {
      if (node.data.project.id === id) {
        rows.push(node);
      }
    });
    if (rows.length) {
      this.gridApi.updateRowData({ remove: rows });
    }
  };

  onSearch = e => this.setState({ filter: e.target.value });

  render() {
    const samples = this.state.samples.filter(s =>
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
        <CartSamplesWrapper className="ag-theme-balham">
          <AgGridReact
            getRowNodeId={data => data.id}
            animateRows={true}
            headerHeight={0}
            columnDefs={this.columnDefs}
            rowData={samples}
            frameworkComponents={{ SampleRenderer }}
            onGridReady={this.onGridReady}
            rowHeight={80}
            rowStyle={{ width: "100%" }}
            filter={true}
            suppressRowClickSelection={true}
            suppressCellSelection={true}
            suppressRowHoverHighlight={true}
          />
        </CartSamplesWrapper>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  count: state.cart.count
});

const mapDispatchToProps = dispatch => ({
  displaySample: sample => dispatch(sampleDetailsActions.displaySample(sample)),
  emptyCart: () => dispatch(actions.emptyCart()),
  removeSample: (projectId, sampleId) =>
    dispatch(actions.removeSample(projectId, sampleId)),
  removeProject: id => dispatch(actions.removeProject(id))
});

export const CartSamples = connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);
