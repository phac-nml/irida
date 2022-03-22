import { createAction, createSlice } from "@reduxjs/toolkit";
import { compareRestrictionLevels } from "../../../utilities/restriction-utilities";

/**
 * Sets the selected samples from the list of samples in the cart
 * to be added to the new project
 */
export const setSamples = createAction(
  `newProject/setSamples`,
  ({ samples }) => ({
    payload: {
      samples,
    },
  })
);

/**
 * Sets up the original restrictions to the sample -> project -> metadata restrictions
 */
export const setNewProjectMetadataRestrictions = createAction(
  `newProject/setNewProjectMetadataRestrictions`,
  (metadataRestrictions) => ({
    payload: {
      metadataRestrictions: metadataRestrictions.map((r) => ({
        ...r,
        initial: true,
      })),
    },
  })
);

/**
 * Update one of the metadata restrictions with a specific value
 */
export const updateNewProjectMetadataRestriction = createAction(
  `newProject/updateNewProjectMetadataRestriction`,
  ({ field, value }) => ({
    payload: {
      field,
      value,
    },
  })
);

/**
 * Set up the initial state.
 */
const initialState = {
  samples: [],
  metadataRestrictions: [],
};

const newProjectSlice = createSlice({
  name: "newProject",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(setSamples, (state, action) => {
      state.samples = action.payload.samples;
    });

    builder.addCase(setNewProjectMetadataRestrictions, (state, action) => {
      state.metadataRestrictions = action.payload.metadataRestrictions;
    });

    builder.addCase(updateNewProjectMetadataRestriction, (state, action) => {
      const { field, value } = action.payload;
      const index = state.metadataRestrictions.findIndex(
        (f) => f.fieldKey === field.fieldKey
      );
      if (index >= 0) {
        field.restriction = value;
        field.difference = compareRestrictionLevels(
          state.metadataRestrictions[index].restriction,
          value
        );
        const updatedFields = [...state.metadataRestrictions];
        updatedFields[index] = field;
        state.metadataRestrictions = updatedFields;
      }
    });
  },
});

export default newProjectSlice.reducer;
