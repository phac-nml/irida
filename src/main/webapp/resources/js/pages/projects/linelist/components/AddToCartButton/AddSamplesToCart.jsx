import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Button } from "antd";
import { actions as cartActions } from "../../../../../redux/reducers/cart";

const { i18n } = window.PAGE;

/**
 * UI Button to single that the selected samples should be added to the global cart.
 * @param {object} props
 * @returns {*}
 * @constructor
 */
export function AddSamplesToCartButtonComponent({
  selected,
  addSamplesToCart
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
      <i className="fas fa-cart-plus spaced-right__sm" />
      {i18n.linelist.addToCart}
    </Button>
  );
}

AddSamplesToCartButtonComponent.propTypes = {
  selected: PropTypes.array.isRequired,
  addSamplesToCart: PropTypes.func.isRequired
};

const mapStateToProps = state => ({
  selected: state.entries.selected
});

const mapDispatchToProps = dispatch => ({
  addSamplesToCart: samples => dispatch(cartActions.add(samples))
});

export const AddSamplesToCartButton = connect(
  mapStateToProps,
  mapDispatchToProps
)(AddSamplesToCartButtonComponent);
