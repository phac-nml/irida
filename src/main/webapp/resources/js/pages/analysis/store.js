import { configureStore } from "@reduxjs/toolkit";
import treeReducer from "./redux/treeSlice";

export default configureStore({
  reducer: {
    tree: treeReducer
  },
})