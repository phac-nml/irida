import React from "react";
import styled from "styled-components";
import PropTypes from "prop-types";
import { CartSamples } from "./CartSamples";
import { SampleDetails } from "../../../components/SampleDetails";
import { CartTools } from "./CartTools";
import { COLOR_BACKGROUND_LIGHT } from "../../../styles/colours";
import { SPACE_MD } from "../../../styles/spacing";

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
  background-color: ${COLOR_BACKGROUND_LIGHT};
  padding: ${SPACE_MD};
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

Cart.propTypes = {
  count: PropTypes.number
};
