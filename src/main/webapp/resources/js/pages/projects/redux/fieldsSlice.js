import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getMetadataFieldsForProject } from "../../../apis/metadata/field";
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
  },
  {
    condition(projectId, { getState }) {
      const { fields } = getState();
      if (fields.requests[projectId]) {
        // Already fetched or in progress, don't need to re-fetch
        return false;
      }
    },
  }
);

/**
 * Redux store slice for metadata fields.
 * @type {Slice<{fields: undefined, loading: boolean, selected: *[]}, {}, string>}
 */
export const fieldsSlice = createSlice({
  name: "fields",
  initialState: {
    selected: [],
    loading: true,
    requests: {},
  },
  reducers: {},
  extraReducers: {
    [fetchFieldsForProject.fulfilled]: (state, { meta, payload }) => {
      const requests = { ...state.requests };
      delete requests[meta.args];

      state.requests = requests;
      state.fields = payload;
      state.loading = false;
    },
    [fetchFieldsForProject.pending]: (state, { meta }) => {
      state.requests[meta.arg] = meta.requestId;
    },
  },
});

export default fieldsSlice.reducer;
