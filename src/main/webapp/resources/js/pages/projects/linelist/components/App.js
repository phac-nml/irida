import React from "react";
import { Provider } from "react-redux";
import { getStore } from "../redux/getStore";
import { LineListContainer } from "./LineList";
import { initializeApp } from "../redux/modules/app";

const store = getStore();

export const App = () => (
  <Provider store={store}>
    <LineListContainer />
  </Provider>
);

const CURRENT_PROJECT_ID = window.project.id;
store.dispatch(initializeApp(CURRENT_PROJECT_ID));
