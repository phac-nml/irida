import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import styled from "styled-components";
import { SPACE_SM } from "../../../styles/spacing";
import {
  blue6,
  COLOR_BORDER_LIGHT,
  grey1,
  grey3,
  red4,
  red6
} from "../../../styles/colors";
import AutoSizer from "react-virtualized-auto-sizer";
import { Button, Input } from "antd";
import { FixedSizeList as VList } from "react-window";
import { getI18N } from "../../../utilities/i18n-utilities";
import { actions } from "../../../redux/reducers/cart";
import { sampleDetailsActions } from "../../../components/SampleDetails";
import { SampleRenderer } from "./SampleRenderer";

const { Search } = Input;

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 400px;
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

const CartSamplesWrapper = styled.div`
  flex-grow: 1;
`;

const ButtonsPanelBottom = styled.div`
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
    color: ${grey1};
  }
`;

function CartSamplesComponent({
  samples,
  applyFilter,
  emptyCart,
  displaySample,
  removeSample,
  removeProject
}) {
  const filterSamples = e => applyFilter(e.target.value);

  const removeOneSample = sample => removeSample(sample.project.id, sample.id);

  const removeOneProject = id => removeProject(id);

  const renderSample = ({ index, data, style }) => (
    <SampleRenderer
      rowIndex={index}
      data={samples[index]}
      style={style}
      displaySample={displaySample}
      removeSample={removeOneSample}
      removeProject={removeOneProject}
    />
  );

  return (
    <Wrapper>
      <CartTools>
        <Search onChange={filterSamples} />
      </CartTools>
      <CartSamplesWrapper className="t-samples-list">
        <AutoSizer>
          {({ height = 600, width = 400 }) => (
            <VList
              itemCount={samples.length}
              itemSize={75}
              height={height}
              width={width}
            >
              {renderSample}
            </VList>
          )}
        </AutoSizer>
      </CartSamplesWrapper>
      <ButtonsPanelBottom>
        <EmptyCartButton
          className="t-empty-cart-btn"
          type="danger"
          block
          onClick={emptyCart}
        >
          {getI18N("cart.clear")}
        </EmptyCartButton>
      </ButtonsPanelBottom>
    </Wrapper>
  );
}

CartSamplesComponent.propTypes = {
  displaySample: PropTypes.func.isRequired,
  removeSample: PropTypes.func.isRequired,
  removeProject: PropTypes.func.isRequired
};

const mapStateToProps = state => ({
  samples: state.cart.filteredSamples
});

const mapDispatchToProps = dispatch => ({
  applyFilter: filter => dispatch(actions.applyFilter(filter)),
  emptyCart: () => dispatch(actions.emptyCart()),
  displaySample: sample => dispatch(sampleDetailsActions.displaySample(sample)),
  removeSample: (projectId, sampleId) =>
    dispatch(actions.removeSample(projectId, sampleId)),
  removeProject: id => dispatch(actions.removeProject(id))
});

const CartSamples = connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);

export default CartSamples;
