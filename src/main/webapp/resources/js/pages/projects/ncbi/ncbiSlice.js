import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getNCBIPlatforms } from "../../../apis/ncbi/ncbi";

export const fetchPlatforms = createAsyncThunk(`ncbi/platforms`, async () => {
  const platforms = await getNCBIPlatforms();
  return Object.keys(platforms).map((platform) => ({
    value: platform,
    label: platform,
    children: platforms[platform].map((child) => ({
      value: child,
      label: child,
    })),
  }));
});

const ncbiSlice = createSlice({
  name: "ncbi",
  initialState: {
    platforms: undefined,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(fetchPlatforms.fulfilled, (state, action) => {
      state.platforms = action.payload;
    });
  },
});

export default ncbiSlice.reducer;
