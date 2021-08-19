import { createSlice } from "@reduxjs/toolkit";

const initialState = (() => {
  const stringData = window.sessionStorage.getItem("share");
  const { samples, projectId: currentProject } = JSON.parse(stringData);
  return { samples, currentProject };
})();

const shareSlice = createSlice({
  name: "share",
  initialState,
});

export default shareSlice.reducer;
