import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { updateUserRoleOnProject } from "../../../apis/projects/members";

export const updateMemberRole = createAsyncThunk(
  `members/updateMemberRole`,
  async ({ id, role }, { rejectWithValue }) => {
    try {
      const message = await updateUserRoleOnProject({ id, role });
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
