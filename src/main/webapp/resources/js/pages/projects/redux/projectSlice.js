import { createSlice } from "@reduxjs/toolkit";

export const projectSlice = createSlice({
  name: "project",
  initialState: {
    canManage: window.project?.canManage || false,
    defaultMetadataTemplateId: window.project?.defaultMetadataTemplateId,
  },
  reducers: {
    // Reducer to update the project default metadata template id
    updateProjectDefaultMetadataTemplateId(state, action) {
      state.defaultMetadataTemplateId = action.payload;
    },
  },
  extraReducers: {},
});

export const { updateProjectDefaultMetadataTemplateId } = projectSlice.actions;

export default projectSlice.reducer;
