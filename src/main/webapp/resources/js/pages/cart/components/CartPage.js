import React from "react";
import PropTypes from "prop-types";
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

CartPageComponent.propTypes = {
  initialized: PropTypes.bool.isRequired,
  count: PropTypes.number
};

const mapStateToProps = state => ({
  count: state.cart.count,
  initialized: state.cart.initialized
});

const mapDispatchToProps = dispatch => ({});

export const CartPage = connect(
  mapStateToProps,
  mapDispatchToProps
)(CartPageComponent);
