import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getMetadataFieldsForProject,
  getMetadataRestrictions,
} from "../../../apis/metadata/field";
import { addKeysToList } from "../../../utilities/http-utilities";

/**
 * Redux Thunk for fetching all the metadata fields on a project.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const fetchFieldsForProject = createAsyncThunk(
  `fields/fetchFieldsForProject`,
  async (projectId) => {
    const fields = await getMetadataFieldsForProject(projectId);
    return addKeysToList(fields, "field", "id");
  }
);

export const fetchFieldsRestrictions = createAsyncThunk(
  `fields/fetchFieldsRestrictions`,
  async () => {
    const restrictions = await getMetadataRestrictions();
    console.log(restrictions);
    return {
      restrictions: addKeysToList(restrictions, "restriction", "value"),
    };
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
      console.log(action);
      state.restrictions = action.payload.restrictions;
    },
  },
});

export default fieldsSlice.reducer;
