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
  },
});

export default fieldsSlice.reducer;
