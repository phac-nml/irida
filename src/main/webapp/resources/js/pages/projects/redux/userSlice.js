import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { fetchCurrentUserDetails } from "../../../apis/users/user";

export const getCurrentUserDetails = createAsyncThunk(
  `user/getCurrentUserDetails`,
  async () => {
    const details = await fetchCurrentUserDetails();
    return { details };
  }
);

export const userSlice = createSlice({
  name: "userSlice",
  initialState: {},
  reducers: {},
  extraReducers: {
    [getCurrentUserDetails.fulfilled]: (state, action) => {
      return { ...state, ...action.payload.details };
    },
  },
});

export default userSlice.reducer;
