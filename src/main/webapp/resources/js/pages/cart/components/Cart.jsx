import React from "react";
import styled from "styled-components";
import CartSamples from "./CartSamples";
import SampleDetails from "../../../components/SampleDetails";
import CartTools from "./CartTools";
import { COLOURS, SPACING } from "../../../styles";

const Wrapper = styled.div`
  display: flex;
  height: 100%;
  width: 100%;
`;

const Sidebar = styled.div`
  height: 100%;
  width: 400px;
`;

const Content = styled.div`
  height: 100%;
  flex-grow: 1;
  background-color: ${COLOURS.BG_LIGHT};
  padding: ${SPACING.DEFAULT};
`;

export default function Cart({ count }) {
  return (
    <Wrapper>
      <Sidebar>
        <CartSamples count={count} />
      </Sidebar>
      <Content>
        <CartTools />
      </Content>
      <SampleDetails />
    </Wrapper>
  );
}
