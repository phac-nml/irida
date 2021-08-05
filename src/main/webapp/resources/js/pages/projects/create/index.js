import React from "react";
import { Provider } from "react-redux";
import { CreateProjectLayout } from "./CreateProjectLayout";
import store from "./store";

export function CreateNewProject({ children }) {
  return (
    <Provider store={store}>
      <CreateProjectLayout>{children}</CreateProjectLayout>
    </Provider>
  );
}
