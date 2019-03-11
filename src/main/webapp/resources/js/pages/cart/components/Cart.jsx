import React from "react";
import styled from "styled-components";
import PropTypes from "prop-types";
import { CartSamples } from "./CartSamples";
import { SampleDetails } from "../../../components/SampleDetails";
import { CartTools } from "./CartTools";
import { grey4 } from "../../../styles/colors";

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
  border-right: 1px solid ${grey4};
`;

export default function Cart({ count }) {
  return (
    <Wrapper>
      <Content>
        <CartTools />
      </Content>
      <Sidebar>
        <CartSamples count={count} />
      </Sidebar>
      <SampleDetails />
    </Wrapper>
  );
}

Cart.propTypes = {
  count: PropTypes.number
};
