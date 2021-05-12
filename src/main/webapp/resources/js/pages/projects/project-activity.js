import { configureStore } from "@reduxjs/toolkit";
import { Typography } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { projectApi } from "../../apis/projects/project";

const store = configureStore({
  reducer: {
    [projectApi.reducerPath]: projectApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(projectApi.middleware),
});

function ProjectActivity() {
  return (
    <>
      <Typography.Title level={2}>Project Activity</Typography.Title>
    </>
  );
}

render(
  <Provider store={store}>
    <ProjectActivity />
  </Provider>,
  document.querySelector("#root")
);
