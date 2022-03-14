import { createAction, createSlice } from "@reduxjs/toolkit";
import { compareRestrictionLevels } from "../../../utilities/restriction-utilities";

/**
 * Sets up the original restrictions to be the same as what are on the source project.
 * This will change once a target project is selected
 */
export const setNewProjectMetadataRestrictions = createAction(
  `metadataRestriction/setNewProjectMetadataRestrictions`,
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
  `metadataRestriction/updateNewProjectMetadataRestriction`,
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
  metadataRestrictions: [],
};

const metadataRestrictionSlice = createSlice({
  name: "metadataRestriction",
  initialState,
  extraReducers: (builder) => {
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

export default metadataRestrictionSlice.reducer;
