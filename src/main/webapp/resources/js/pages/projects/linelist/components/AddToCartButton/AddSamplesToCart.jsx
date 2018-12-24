import React from "react";
import PropTypes from "prop-types";
import { Button } from "antd";

const { i18n } = window.PAGE;

/**
 * UI Button to single that the selected samples should be added to the global cart.
 * @param {object} props
 * @returns {*}
 * @constructor
 */
export function AddSamplesToCartButton(props) {
  return (
    <Button
      tour="tour-cart"
      disabled={props.selectedCount === 0}
      onClick={() => props.addSamplesToCart()}
    >
      <i className="fas fa-cart-plus spaced-right__sm" />
      {i18n.linelist.addToCart}
    </Button>
  );
}

AddSamplesToCartButton.propTypes = {
  selectedCount: PropTypes.number.isRequired,
  addSamplesToCart: PropTypes.func.isRequired
};
