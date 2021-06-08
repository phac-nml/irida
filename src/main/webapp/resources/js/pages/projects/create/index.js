import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { CreateProjectLayout } from "./CreateProjectLayout";
import store from "./store";

render(
  <Provider store={store}>
    <CreateProjectLayout />
  </Provider>,
  document.querySelector("#root")
);
