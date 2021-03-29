import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getMetadataFieldsForProject,
  getMetadataRestrictions,
  patchProjectMetadataFieldRestriction,
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
  async ({ projectId, fieldKey, projectRole }) => {
    const message = await patchProjectMetadataFieldRestriction({
      projectId,
      fieldKey,
      projectRole,
    });
    return { message, fieldKey, projectRole };
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
    [updateProjectFieldRestriction.fulfilled]: (state, action) => {
      const index = state.fields.findIndex(
        (field) => field.key === action.payload.fieldKey
      );
      console.log(index);
      state.fields[index].restriction = action.payload.restriction;
    },
  },
});

export default fieldsSlice.reducer;
