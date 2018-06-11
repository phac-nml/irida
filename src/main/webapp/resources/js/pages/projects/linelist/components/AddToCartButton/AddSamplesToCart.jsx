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
      style={{ marginLeft: "4px" }}
      onClick={() => props.addSamplesToCart()}
      icon="shopping-cart"
    >
      {i18n.linelist.addToCart}
    </Button>
  );
}

AddSamplesToCartButton.propTypes = {
  addSamplesToCart: PropTypes.func.isRequired
};
