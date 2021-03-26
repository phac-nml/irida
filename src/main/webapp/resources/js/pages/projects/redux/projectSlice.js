import { createSlice } from "@reduxjs/toolkit";

export const projectSlice = createSlice({
  name: "project",
  initialState: {
    canManage: window.project?.canManage || false,
  },
  reducers: {},
  extraReducers: {},
});

export default projectSlice.reducer;
