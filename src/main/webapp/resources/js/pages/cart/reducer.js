export const TYPES = {
  SAMPLES_LOADED: "CART/SAMPLES_LOADED"
};

const initialState = {
  samples: []
};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case TYPES.SAMPLES_LOADED:
      return { ...state, samples: [...state.samples, ...action.samples] };
    default:
      return { ...state };
  }
};

export const actions = {
  samplesLoaded: samples => ({ type: TYPES.SAMPLES_LOADED, samples })
};
