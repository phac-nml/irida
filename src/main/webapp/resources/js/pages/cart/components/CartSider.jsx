import React from "react";

import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Layout } from "antd";
import CartSamples from "./CartSamples";
import { grey2 } from "../../../styles/colors";
import CartNotification from "./CartNotification";

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
        <CartNotification
          type={"shopping"}
          text={i18n("CartEmpty.heading")}
        />
      ) : loaded ? (
        <CartSamples />
      ) : (
        <CartNotification
          text={i18n("cart.noneMatchingFilter")}
          type={"warning"}
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
