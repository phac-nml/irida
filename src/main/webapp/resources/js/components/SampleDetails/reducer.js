export const sampleDetailsTypes = {
  DISPLAY_DETAILS: "SAMPLE_DETAILS/DISPLAY",
  DETAILS_LOADED: "SAMPLE_DETAILS/LOADED",
  HIDE_DETAILS: "SAMPLE_DETAILS/HIDE"
};

const initialState = {
  sample: undefined,
  metadata: undefined,
  modifiable: false,
  visible: false
};

export const sampleDetailsReducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case sampleDetailsTypes.HIDE_DETAILS:
      return {
        ...state,
        sample: undefined,
        metadata: undefined,
        visible: false
      };
    case sampleDetailsTypes.DETAILS_LOADED:
      return {
        ...state,
        sample: action.sample,
        metadata: action.metadata,
        modifiable: action.modifiable,
        visible: true
      };
    default:
      return { ...state };
  }
};

export const sampleDetailsActions = {
  displaySample: sample => ({
    type: sampleDetailsTypes.DISPLAY_DETAILS,
    sample
  }),
  closeDisplay: () => ({
    type: sampleDetailsTypes.HIDE_DETAILS
  }),
  sampleLoaded: (sample, metadata, modifiable) => ({
    type: sampleDetailsTypes.DETAILS_LOADED,
    sample,
    metadata,
    modifiable
  })
};
