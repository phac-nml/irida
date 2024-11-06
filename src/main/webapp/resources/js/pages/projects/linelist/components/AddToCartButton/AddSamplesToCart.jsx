import React from "react";

import { connect } from "react-redux";
import { Button } from "antd";
import { actions as cartActions } from "../../../../../redux/reducers/cart";
import { IconShoppingCart } from "../../../../../components/icons/Icons";

/**
 * UI Button to single that the selected samples should be added to the global cart.
 * @param {object} props
 * @returns {*}
 * @constructor
 */
export function AddSamplesToCartButtonComponent({
  selected,
  addSamplesToCart,
}) {
  function addToCart() {
    addSamplesToCart(selected);
  }
  return (
    <Button
      tour="tour-cart"
      disabled={selected.length === 0}
      onClick={addToCart}
    >
      <IconShoppingCart />
      {i18n("linelist.addToCart")}
    </Button>
  );
}

const mapStateToProps = (state) => ({
  selected: state.entries.selected,
});

const mapDispatchToProps = (dispatch) => ({
  addSamplesToCart: (samples) => dispatch(cartActions.add(samples)),
});

export const AddSamplesToCartButton = connect(
  mapStateToProps,
  mapDispatchToProps
)(AddSamplesToCartButtonComponent);
