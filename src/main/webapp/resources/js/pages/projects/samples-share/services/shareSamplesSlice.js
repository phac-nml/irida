import { createSlice } from "@reduxjs/toolkit";

const initialState = (() => {
  const shared = sessionStorage.getItem("share");
  return JSON.parse(shared);
  // sessionStorage.removeItem("share");
})();

export const sharedSamplesSlice = createSlice({
  name: "sharedSamples",
  initialState,
  reducers: {},
});

export default sharedSamplesSlice.reducer;
