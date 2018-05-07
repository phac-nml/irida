import React from "react";
import { Provider } from "react-redux";
import { getStore } from "../../../../redux/getStore";
import { actions } from "../../../../redux/reducers/app";
import {
  fieldsReducer as fields,
  entriesReducer as entries
} from "../reducers";
import * as sagas from "../sagas";
import LineList from "../components/LineList/LineListContainer";

const store = getStore({ fields, entries }, sagas);

export const App = () => (
  <Provider store={store}>
    <LineList />
  </Provider>
);

const CURRENT_PROJECT_ID = window.project.id;
store.dispatch(actions.initialize(CURRENT_PROJECT_ID));
