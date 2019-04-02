import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { FixedSizeList as VList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import { Button, Icon, Input, List, Layout } from "antd";
import styled from "styled-components";
import { actions } from "../../../redux/reducers/cart";
import { sampleDetailsActions } from "../../../components/SampleDetails/reducer";
import { SampleRenderer } from "./SampleRenderer";
import { getCartIds, getSamplesForProject } from "../../../apis/cart/cart";
import {
  blue6,
  COLOR_BORDER_LIGHT,
  grey1,
  grey2,
  grey3,
  grey5
} from "../../../styles/colors";
import { SPACE_MD, SPACE_SM } from "../../../styles/spacing";
import { getI18N } from "../../../utilities/i18n-utilties";
const { Sider } = Layout;

const { Search } = Input;

const CartSamplesWrapper = styled.div`
  flex-grow: 1;
`;

const CartTools = styled.div`
  padding: 0 ${SPACE_SM};
  height: 65px;
  border-bottom: 1px solid ${COLOR_BORDER_LIGHT};
  display: flex;
  align-items: center;

  .ant-input {
    background-color: ${grey1};

    &:hover {
      background-color: ${grey3};
    }

    &:focus {
      border: 1px solid ${blue6};
      background-color: ${grey1};
    }
  }
`;

class CartSamplesComponent extends React.Component {
  static propTypes = {
    count: PropTypes.number.isRequired,
    displaySample: PropTypes.func.isRequired,
    removeSample: PropTypes.func.isRequired,
    removeProject: PropTypes.func.isRequired
  };

  samples = [];
  state = { filter: "", samples: [], loaded: false };

  componentDidMount() {
    // Fetch the samples, since no samples will be added we do not need redux for them.
    getCartIds().then(({ ids }) =>
      ids.forEach(id => {
        getSamplesForProject(id).then(result => {
          const samples = [...this.state.samples, ...result];
          this.setState({ samples, loaded: true }, () => {
            this.samples = samples;
          });
        });
      })
    );
  }

  removeSample = sample => {
    this.props.removeSample(sample.project.id, sample.id);
    const index = this.samples.findIndex(s => s.id === sample.id);
    this.samples.splice(index, 1);
    this.setState({
      samples: this.samples.filter(s => s.label.includes(this.state.filter))
    });
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

  onSearch = e =>
    this.setState({
      samples: this.samples.filter(s => s.label.includes(e.target.value)),
      filter: e.target.value
    });

  renderSample = ({ index, data, style }) => {
    const sample = this.state.samples[index];
    return (
      <SampleRenderer
        rowIndex={index}
        data={sample}
        style={style}
        displaySample={this.props.displaySample}
        removeSample={this.removeSample}
        removeProject={this.removeProject}
      />
    );
  };

  render() {
    const { samples, filter } = this.state;

    return (
      <Sider
        width={400}
        trigger={null}
        collapsible
        collapsed={this.props.collapsed}
        collapsedWidth={0}
        style={{ backgroundColor: grey1 }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            height: "100%",
            width: 400
          }}
        >
          <CartTools>
            <Search
              allowClear
              style={{ width: "100%" }}
              onChange={this.onSearch}
              value={filter}
            />
          </CartTools>
          <CartSamplesWrapper>
            {samples.length > 0 ? (
              <AutoSizer>
                {({ height, width }) => (
                  <List itemLayout="vertical">
                    <VList
                      itemCount={samples.length}
                      itemSize={95}
                      height={height}
                      width={width}
                    >
                      {this.renderSample}
                    </VList>
                  </List>
                )}
              </AutoSizer>
            ) : this.state.loaded ? (
              <div
                style={{
                  fontSize: 30,
                  color: blue6,
                  justifyContent: "center",
                  alignItems: "center",
                  flexDirection: "column",
                  height: 300,
                  display: "flex"
                }}
              >
                <div>
                  <Icon type="warning" style={{ fontSize: 60 }} />
                </div>
                <div>{getI18N("cart.noneMatchingFilter")}</div>
              </div>
            ) : null}
          </CartSamplesWrapper>
          <div
            style={{
              height: 60,
              padding: SPACE_SM,
              display: "flex",
              justifyContent: "center",
              borderTop: `1px solid ${COLOR_BORDER_LIGHT}`,
              alignItems: "center"
            }}
          >
            <Button type="danger" block onClick={this.props.emptyCart}>
              {getI18N("cart.clear")}
            </Button>
          </div>
        </div>
      </Sider>
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

const CartSamples = connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);

export default CartSamples;
