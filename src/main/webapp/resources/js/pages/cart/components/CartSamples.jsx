import { Button, Empty, Input, notification, Space, Spin } from "antd";
import React from "react";
import { FixedSizeList as VList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import styled from "styled-components";
import {
  useEmptyMutation,
  useGetCartQuery,
  useRemoveProjectMutation,
  useRemoveSampleMutation,
} from "../../../apis/cart/cart";
import { IconShoppingCart } from "../../../components/icons/Icons";
import { BORDERED_LIGHT } from "../../../styles/borders";
import { blue6, grey1, grey3, red4, red6 } from "../../../styles/colors";
import { SPACE_SM } from "../../../styles/spacing";
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
  border-bottom: ${BORDERED_LIGHT};
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
  border-top: ${BORDERED_LIGHT};
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

/**
 * React component to display a list of samples that are currently in the cart.
 * @param {function} displaySample - function to open modal to display the sample details
 * @returns {JSX.Element}
 * @constructor
 */
export default function CartSamples({ displaySample }) {
  const [samples, setSamples] = React.useState([]);
  const { data: allSamples, isFetching, refetch } = useGetCartQuery();
  const [emptyCart] = useEmptyMutation();
  const [removeSample] = useRemoveSampleMutation();
  const [removeProject] = useRemoveProjectMutation();

  React.useEffect(() => {
    setSamples(allSamples);
  }, [allSamples]);

  const filterSamples = (event) => {
    setSamples(
      allSamples.filter((sample) =>
        sample.label.toLowerCase().includes(event.target.value.toLowerCase())
      )
    );
  };

  const removeOneProject = (id) =>
    removeProject({ id }).then(({ data }) => {
      notification.success({ message: data.message });
      refetch();
    });
  const removeOneSample = (sampleId) =>
    removeSample({ sampleId }).then(({ data }) => {
      notification.success({ message: data.message });
      refetch();
    });

  const renderSample = ({ index, data, style }) => {
    const sample = samples[index];
    return (
      <SampleRenderer
        rowIndex={index}
        data={sample}
        style={style}
        displaySample={displaySample}
        removeSample={() => removeOneSample(sample.id)}
        removeProject={removeOneProject}
        refetch={refetch}
      />
    );
  };

  const empty = () => emptyCart();

  return (
    <Wrapper>
      <CartTools>
        <Search onChange={filterSamples} />
      </CartTools>
      {isFetching ? (
        <Space size="middle">
          <Spin size="large" />
        </Space>
      ) : !samples || samples.length === 0 ? (
        <Empty
          image={<IconShoppingCart style={{ fontSize: 100, color: blue6 }} />}
          description={i18n("CartEmpty.heading")}
        />
      ) : (
        <>
          <CartSamplesWrapper className="t-samples-list">
            <AutoSizer>
              {({ height = 600, width = 400 }) => (
                <VList
                  itemCount={samples.length}
                  itemSize={50}
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
              onClick={empty}
            >
              {i18n("cart.clear")}
            </EmptyCartButton>
          </ButtonsPanelBottom>
        </>
      )}
    </Wrapper>
  );
}
