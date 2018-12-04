export const TYPES = {
  SAMPLES_LOADED: "CART/SAMPLES_LOADED",
  SAMPLE_SHOW: "CART/SHOW_SAMPLE",
  SAMPLE_HIDE: "CART/HIDE_SAMPLE"
};

const initialState = {
  samples: [],
  sampleVisible: false
};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case TYPES.SAMPLES_LOADED:
      return { ...state, samples: [...state.samples, ...action.samples] };
    case TYPES.SAMPLE_SHOW:
      return { ...state, sampleVisible: true };
    case TYPES.SAMPLE_HIDE:
      return { ...state, sampleVisible: false };
    default:
      return { ...state };
  }
};

export const actions = {
  samplesLoaded: samples => ({ type: TYPES.SAMPLES_LOADED, samples }),
  displaySample: sample => ({ type: TYPES.SAMPLE_SHOW, sample }),
  hideSample: () => ({ type: TYPES.SAMPLE_HIDE })
};
