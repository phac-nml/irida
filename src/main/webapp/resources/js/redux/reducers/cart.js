import { CART } from "../../utilities/events-utilities";

export const types = {
  INITIALIZED: "CART/INITIALIZED",
  ADD: "CART/ADD",
  UPDATED: "CART/UPDATED",
  CART_EMPTY: "CART/EMPTY",
  CART_EMPTY_SUCCESS: "CART/EMPTY_SUCCESS",
  REMOVE_SAMPLE: "CART/REMOVE_SAMPLE"
};

const initialState = { count: 0, initialized: false };

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.INITIALIZED:
      return { ...state, ...{ count: action.count, initialized: true } };
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
      return { ...state, ...{ count: action.response.count } };
    case types.CART_EMPTY_SUCCESS:
      return { ...state, count: 0 };
    default:
      return { ...state };
  }
};

export const actions = {
  initialized: count => ({ type: types.INITIALIZED, count }),
  add: samples => ({ type: types.ADD, samples }),
  updated: response => ({ type: types.UPDATED, response }),
  emptyCart: () => ({ type: types.CART_EMPTY }),
  removeSample: (projectId, sampleId) => ({
    type: types.REMOVE_SAMPLE,
    payload: {
      projectId,
      sampleId
    }
  })
};
