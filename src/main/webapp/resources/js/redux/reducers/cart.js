import { fromJS } from "immutable";
import { CART } from "../../utilities/events-utilities";

export const types = {
  ADD: "CART/ADD",
  UPDATED: "CART/UPDATED"
};

const initialState = fromJS({ count: 0 });

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.UPDATED:
      /*
      Since the cart is not currently a react component, setting the state does
      nothing.  We are going to use a CustomEvent so that we can communicate with
      the current AngularJS controller for the navigation.
       */
      document.dispatchEvent(
        new CustomEvent(CART.UPDATED, {
          detail: action.response
        })
      );
      return state.setIn(["count"], action.response.count);
    default:
      return state;
  }
};

export const actions = {
  add: samples => ({ type: types.ADD, samples }),
  updated: response => ({ type: types.UPDATED, response })
};
