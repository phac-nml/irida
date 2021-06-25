import { createReducer } from "@reduxjs/toolkit";

const initialState = (() => {
  const sharedString = sessionStorage.getItem("share");
  return sharedString ? JSON.parse(sharedString) : {};
})();

export const rootReducer = createReducer(initialState, (builder) => {});
