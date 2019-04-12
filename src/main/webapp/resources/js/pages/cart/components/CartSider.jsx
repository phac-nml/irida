import React, { useState, useEffect } from "react";
import styled from "styled-components";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { FixedSizeList as VList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import { Button, Icon, Input, List, Layout } from "antd";
import { actions } from "../../../redux/reducers/cart";
import { sampleDetailsActions } from "../../../components/SampleDetails/reducer";
import CartSamples from "./CartSamples";
import { SampleRenderer } from "./SampleRenderer";
import { getCartIds, getSamplesForProjects } from "../../../apis/cart/cart";
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
import CartNotification from "./CartNotification";
const { Sider } = Layout;

const { Search } = Input;

const SiderInner = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 400px;
`;

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
        <CartNotification
          icon="shopping-cart"
          text={getI18N("CartEmpty.heading")}
        />
      ) : loaded ? (
        <CartSamples />
      ) : (
        <CartNotification
          text={getI18N("cart.noneMatchingFilter")}
          icon="warning"
        />
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
