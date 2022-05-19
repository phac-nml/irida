import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getNCBIPlatforms,
  getNCBISelections,
  getNCBISources,
  getNCBIStrategies,
} from "../../../apis/ncbi/ncbi";

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

export const fetchSources = createAsyncThunk(`ncbi/sources`, async () => {
  return await getNCBISources();
});

export const fetchStrategies = createAsyncThunk(`ncbi/strategies`, async () => {
  return await getNCBIStrategies();
});

export const fetchSelections = createAsyncThunk(`ncbi/selections`, async () => {
  return await getNCBISelections();
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
    builder.addCase(fetchSources.fulfilled, (state, action) => {
      state.sources = action.payload;
    });
    builder.addCase(fetchStrategies.fulfilled, (state, action) => {
      state.strategies = action.payload;
    });
    builder.addCase(fetchSelections.fulfilled, (state, action) => {
      state.selections = action.payload;
    });
  },
});

export default ncbiSlice.reducer;
