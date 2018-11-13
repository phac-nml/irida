import React from "react";
import { Provider } from "react-redux";
import { getStore } from "../../../../redux/getStore";
import { actions } from "../../../../redux/reducers/app";
import * as reducers from "../reducers";
import * as sagas from "../sagas";
import LineList from "../components/LineList/LineListContainer";

const store = getStore(reducers, sagas);

export const App = () => (
  <Provider store={store}>
    <LineList />
  </Provider>
);

const CURRENT_PROJECT_ID = window.project.id;
store.dispatch(actions.initialize({ id: CURRENT_PROJECT_ID }));
