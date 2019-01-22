import { types as globalCartTypes } from "../../redux/reducers/cart";

export const cartPageTypes = {
  SAMPLES_LOADED: "CART/SAMPLES_LOADED",
  CART_EMPTY: "CART/EMPTY",
  CART_EMPTY_SUCCESS: "CART/EMPTY_SUCCESS",
  REMOVE_SAMPLE: "CART/REMOVE_SAMPLE",
  REMOVE_SAMPLE_SUCCESS: "CART/REMOVE_SAMPLE_SUCCESS"
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
    case cartPageTypes.REMOVE_SAMPLE_SUCCESS:
      return {
        ...state,
        samples: state.samples.filter(s => s.id !== action.payload.id)
      };
    default:
      return { ...state };
  }
};

export const cartPageActions = {
  samplesLoaded: samples => ({ type: cartPageTypes.SAMPLES_LOADED, samples }),
  emptyCart: () => ({ type: cartPageTypes.CART_EMPTY }),
  removeSample: data => ({
    type: cartPageTypes.REMOVE_SAMPLE,
    ...data
  }),
  sampleRemovedSuccess: id => ({
    type: cartPageTypes.REMOVE_SAMPLE_SUCCESS,
    payload: { id }
  })
};
