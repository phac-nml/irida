import React from "react";
import { Provider } from "react-redux";
import { getStore } from "../../../../redux/getStore";
import { actions } from "../../../../redux/reducers/app";
import fields from "../reducers/fields";
import entries from "../reducers/entries";
import { fieldsLoadingSaga } from "../sagas/fields";
import { entriesLoadingSaga } from "../sagas/entries";
import LineList from "./LineList";

const store = getStore(
  { fields, entries },
  { fieldsLoadingSaga, entriesLoadingSaga }
);

export const App = () => (
  <Provider store={store}>
    <LineList />
  </Provider>
);

const CURRENT_PROJECT_ID = window.project.id;
store.dispatch(actions.initialize(CURRENT_PROJECT_ID));
