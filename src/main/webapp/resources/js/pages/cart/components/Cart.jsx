import React, { lazy, Suspense, useState, useEffect } from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Layout } from "antd";
import { SampleDetailsLoader } from "../../../components/SampleDetails";
import { actions } from "../../../redux/reducers/cart";

const CartSamples = lazy(() => import("./CartSider"));
const CartTools = lazy(() => import("./CartTools"));

const { Content } = Layout;

function CartComponent({ count = 0, loadCart }) {
  const [collapsed, setCollapsed] = useState(false);

  useEffect(() => {
    setCollapsed(count === 0);
    if (count !== 0) {
      loadCart();
    }
  }, [count]);

  const toggleSidebar = () => setCollapsed(!collapsed);

  return (
    <Content style={{ display: "flex", height: "100%" }}>
      <Content style={{ flexGrow: 1 }}>
        <Suspense fallback={<div />}>
          <CartTools
            toggleSidebar={toggleSidebar}
            collapsed={collapsed}
            count={count}
          />
        </Suspense>
      </Content>
      <Suspense fallback={<div style={{ width: 400, height: "100%" }} />}>
        <CartSamples count={count} collapsed={collapsed} />
      </Suspense>
      <SampleDetailsLoader />
    </Content>
  );
}

CartComponent.propTypes = {
  count: PropTypes.number
};

const mapStateToProps = state => ({
  count: state.cart.count,
  initialized: state.cart.initialized
});

const mapDispatchToProps = dispatch => ({
  loadCart: () => dispatch(actions.loadCart())
});

export const Cart = connect(
  mapStateToProps,
  mapDispatchToProps
)(CartComponent);
