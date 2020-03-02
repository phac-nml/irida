import React from "react";

import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Layout } from "antd";
import CartSamples from "./CartSamples";
import { blue6, grey2 } from "../../../styles/colors";
import styled from "styled-components";
import {
  IconExclamationCircle,
  IconShoppingCart
} from "../../../components/icons/Icons";

const Wrapper = styled.div`
  font-size: 30px;
  color: ${blue6};
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const { Sider } = Layout;

function CartSamplesComponent({ count, collapsed, loaded }) {
  return (
    <Sider
      width={400}
      trigger={null}
      collapsible
      collapsed={collapsed}
      collapsedWidth={0}
      style={{ backgroundColor: grey2 }}
    >
      {count === 0 ? (
        <Wrapper>
          <div>
            <IconShoppingCart style={{ fontSize: 120 }} />
          </div>
          <div>{i18n("CartEmpty.heading")}</div>
        </Wrapper>
      ) : loaded ? (
        <CartSamples />
      ) : (
        <Wrapper>
          <div>
            <IconExclamationCircle style={{ fontSize: 120 }} />
          </div>
          <div>{i18n("cart.noneMatchingFilter")}</div>
        </Wrapper>
      )}
    </Sider>
  );
}

CartSamplesComponent.propTypes = {
  count: PropTypes.number.isRequired,
  collapsed: PropTypes.bool.isRequired
};

const mapStateToProps = state => ({
  count: state.cart.count,
  loaded: state.cart.loaded
});

const mapDispatchToProps = dispatch => ({});

const CartSider = connect(
  mapStateToProps,
  mapDispatchToProps
)(CartSamplesComponent);

export default CartSider;
