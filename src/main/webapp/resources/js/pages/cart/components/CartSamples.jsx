import React from "react";
import styled from "styled-components";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { FixedSizeList as VList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import { Button, Icon, Input, List, Layout } from "antd";
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
  grey5,
  red4,
  red6
} from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
import { getI18N } from "../../../utilities/i18n-utilties";
const { Sider } = Layout;

const { Search } = Input;

const SiderInner = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 400px;
`;

const CartSamplesWrapper = styled.div`
  flex-grow: 1;
`;

const ButtonsPanelBotton = styled.div`
  height: 60px;
  padding: ${SPACE_SM};
  border-top: 1px solid ${COLOR_BORDER_LIGHT};
  display: flex;
  justify-content: center;
  align-items: center;
`;

const EmptyCartButton = styled(Button)`
  background-color: ${red4};
  color: ${grey1};

  &:hover {
    background-color: ${red6};
  }
`;

const FilterWarning = styled.div`
  font-size: 30px;
  color: ${blue6};
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
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
    this.samples = this.samples.filter(s => s.project.id !== id);
    this.setState({
      samples: this.samples.filter(s => s.label.includes(this.state.filter))
    });
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
        style={{ backgroundColor: grey2 }}
      >
        <SiderInner>
          <CartTools>
            <Search allowClear onChange={this.onSearch} value={filter} />
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
              <FilterWarning>
                <div>
                  <Icon type="warning" style={{ fontSize: 120 }} />
                </div>
                <div>{getI18N("cart.noneMatchingFilter")}</div>
              </FilterWarning>
            ) : null}
          </CartSamplesWrapper>
          <ButtonsPanelBotton>
            <EmptyCartButton type="danger" block onClick={this.props.emptyCart}>
              {getI18N("cart.clear")}
            </EmptyCartButton>
          </ButtonsPanelBotton>
        </SiderInner>
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
