import React from "react";
import { connect } from "react-redux";
import CartEmpty from "./CartEmpty";
import Cart from "./Cart";

function CartPageComponent({ initialized = true, count = 0 }) {
  if (!initialized) {
    return <div>Loading...</div>;
  } else if (count === 0) {
    return <CartEmpty />;
  }
  return <Cart count={count} />;
}

const mapStateToProps = state => ({
  count: state.cart.count,
  initialized: state.cart.initialized
});

const mapDispatchToProps = dispatch => ({});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CartPageComponent);
