import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getMetadataFieldsForProject,
  getMetadataRestrictions,
  updateProjectMetadataFieldRestriction,
} from "../../../apis/metadata/field";
import { addKeysToList } from "../../../utilities/http-utilities";
import { updateFieldsForAllFieldsTemplate } from "./templatesSlice";

/**
 * Redux Thunk for fetching all the metadata fields on a project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const fetchFieldsForProject = createAsyncThunk(
  `fields/fetchFieldsForProject`,
  async (projectId, { dispatch }) => {
    const fields = await getMetadataFieldsForProject(projectId);
    // Update the fields in the state for the All Fields Template
    dispatch(updateFieldsForAllFieldsTemplate(fields));
    return addKeysToList(fields, "field", "id");
  }
);

export const fetchFieldsRestrictions = createAsyncThunk(
  `fields/fetchFieldsRestrictions`,
  async () => {
    const restrictions = await getMetadataRestrictions();
    return {
      restrictions: addKeysToList(restrictions, "restriction", "value"),
    };
  },
  {
    condition: (_args, { getState }) => {
      const { fields } = getState();
      if ("restrictions" in fields) {
        return false;
      }
    },
  }
);

export const updateProjectFieldRestriction = createAsyncThunk(
  `fields/updateProjectFieldRestriction`,
  async ({ projectId, fieldId, projectRole }) => {
    const { message } = await updateProjectMetadataFieldRestriction({
      projectId,
      fieldId,
      projectRole,
    });
    return { message, fieldId, projectRole };
  }
);

/**
 * Redux store slice for metadata fields.
 * @type {Slice<{fields: undefined, loading: boolean, selected: *[]}, {}, string>}
 */
export const fieldsSlice = createSlice({
  name: "fields",
  initialState: {
    fields: undefined,
    selected: [],
    loading: true,
  },
  reducers: {},
  extraReducers: {
    [fetchFieldsForProject.fulfilled]: (state, action) => ({
      ...state,
      fields: action.payload,
      loading: false,
    }),
    [fetchFieldsRestrictions.fulfilled]: (state, action) => {
      state.restrictions = action.payload.restrictions;
    },
    [updateProjectFieldRestriction.fulfilled]: (state, action) => {
      const index = state.fields.findIndex(
        (field) => field.id === action.payload.fieldId
      );
      state.fields[index].restriction = action.payload.projectRole;
    },
  },
});

export default fieldsSlice.reducer;
