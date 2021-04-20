import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { updateUserRoleOnProject } from "../../../apis/projects/members";

export const updateMemberRole = createAsyncThunk(
  `members/updateMemberRole`,
  async ({ id, role }, { rejectWithValue, getState }) => {
    const { project } = getState();
    console.log(project);
    try {
      const message = await updateUserRoleOnProject({
        projectId: project.id,
        id,
        role,
      });
      return { message };
    } catch (e) {
      return rejectWithValue(e.response.data);
    }
  }
);

export const membersSlice = createSlice({
  name: "membersSlice",
  initialState: {},
  reducers: {},
  extraReducers: {},
});

export default membersSlice.reducer;
