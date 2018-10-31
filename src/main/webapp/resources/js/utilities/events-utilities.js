/**
 * This file is for the storage of Event types so they can have common usage
 * across the platform.
 */

/*
All events related to the cart functionality.
 */
export const CART = {
  UPDATED: "cart:updated",
  ADD: "cart:add"
};

export const cartNotification = detail => {
  document.dispatchEvent(
    new CustomEvent(CART.UPDATED, {
      detail
    })
  );
};
