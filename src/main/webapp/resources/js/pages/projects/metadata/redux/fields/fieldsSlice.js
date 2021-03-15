import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getMetadataFieldsForProject } from "../../../../../apis/metadata/field";
import { addKeysToList } from "../../../../../utilities/http-utilities";

export const fetchFieldsForProject = createAsyncThunk(
  `fields/fetchFieldsForProject`,
  async (projectId) => {
    const fields = await getMetadataFieldsForProject(projectId);
    return addKeysToList(fields, "field", "id");
  }
);

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
