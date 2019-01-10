import { types as globalCartTypes } from "../../redux/reducers/cart";

export const cartPageTypes = {
  SAMPLES_LOADED: "CART/SAMPLES_LOADED",
  CART_EMPTY: "CART/EMPTY",
  CART_EMPTY_SUCCESS: "CART/EMPTY_SUCCESS"
};

const initialState = {
  samples: []
};

export const cartPageReducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case cartPageTypes.SAMPLES_LOADED:
      return { ...state, samples: [...state.samples, ...action.samples] };
    case globalCartTypes.UPDATED:
      return { ...state, samples: [] };
    default:
      return { ...state };
  }
};

export const cartPageActions = {
  samplesLoaded: samples => ({ type: cartPageTypes.SAMPLES_LOADED, samples }),
  emptyCart: () => ({ type: cartPageTypes.CART_EMPTY })
};
