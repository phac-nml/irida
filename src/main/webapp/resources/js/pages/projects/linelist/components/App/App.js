import React from "react";
import { Provider } from "react-redux";
import { getStore } from "../../../../../redux/getStore";
import { actions } from "../../../../../redux/reducers/app";
import { fieldsReducer } from "../../reducers/index";
import { fieldsLoadingSaga } from "../../sagas/index";
import { LineListContainer } from "../LineList/LineListContainer";

const store = getStore({ fields: fieldsReducer }, { fieldsLoadingSaga });

export const App = () => (
  <Provider store={store}>
    <LineListContainer />
  </Provider>
);

const CURRENT_PROJECT_ID = window.project.id;
store.dispatch(actions.initialize(CURRENT_PROJECT_ID));
