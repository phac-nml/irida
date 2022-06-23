import { createAction, createSlice } from "@reduxjs/toolkit";

/**
 * Action to set the sample ids of samples in the cart
 */
export const setCartSampleIds = createAction(
  `cartSamples/setCartSampleIds`,
  ({ sampleIds }) => ({
    payload: { sampleIds },
  })
);

/**
 * Action to add sample id of sample in cart
 */
export const addCartSampleId = createAction(
  `cartSamples/addCartSampleId`,
  ({ sampleId }) => ({
    payload: { sampleId },
  })
);

/**
 * Action to remove sample id from sample ids in the cart
 */
export const removeCartSampleId = createAction(
  `cartSamples/removeCartSampleId`,
  ({ sampleId }) => ({
    payload: { sampleId },
  })
);

/**
 * Set up the initial state.
 */
const initialState = (() => {
  return {
    sampleIds: null,
    loading: true,
  };
})();

const cartSamplesSlice = createSlice({
  name: "cartSamples",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(setCartSampleIds, (state, action) => {
      state.sampleIds = action.payload.sampleIds;
      state.loading = false;
    });

    builder.addCase(addCartSampleId, (state, action) => {
      state.sampleIds = [...state.sampleIds, parseInt(action.payload.sampleId)];
    });

    builder.addCase(removeCartSampleId, (state, action) => {
      state.sampleIds = state.sampleIds.filter(
        (sampleId) => sampleId !== parseInt(action.payload.sampleId)
      );
    });
  },
});

export default cartSamplesSlice.reducer;
