import { types as globalCartTypes } from "../../redux/reducers/cart";

export const TYPES = {
  SAMPLES_LOADED: "CART/SAMPLES_LOADED",
  SAMPLE_SHOW: "CART/SHOW_SAMPLE",
  SAMPLE_HIDE: "CART/HIDE_SAMPLE",
  CART_EMPTY: "CART/EMPTY",
  CART_EMPTY_SUCCESS: "CART/EMPTY_SUCCESS"
};

const initialState = {
  samples: [],
  sampleVisible: false,
  sample: undefined
};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case TYPES.SAMPLES_LOADED:
      return { ...state, samples: [...state.samples, ...action.samples] };
    case TYPES.SAMPLE_SHOW:
      return { ...state, sample: action.sample, sampleVisible: true };
    case TYPES.SAMPLE_HIDE:
      return { ...state, sample: undefined, sampleVisible: false };
    case globalCartTypes.UPDATED:
      return { ...state, samples: [] };
    default:
      return { ...state };
  }
};

export const actions = {
  samplesLoaded: samples => ({ type: TYPES.SAMPLES_LOADED, samples }),
  displaySample: sample => ({ type: TYPES.SAMPLE_SHOW, sample }),
  hideSample: () => ({ type: TYPES.SAMPLE_HIDE }),
  emptyCart: () => ({ type: TYPES.CART_EMPTY })
};
