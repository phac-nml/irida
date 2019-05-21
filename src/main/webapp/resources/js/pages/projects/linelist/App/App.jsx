import React, { useEffect, useState } from "react";
import { Provider } from "react-redux";
import { getStore } from "../../../../redux/getStore";
import { actions } from "../../../../redux/reducers/app";
import * as reducers from "../reducers";
import * as sagas from "../sagas";
import LineList from "../components/LineList/LineListContainer";
import { IntlProvider } from "react-intl";

const store = getStore(reducers, sagas);

export function App() {
  const [translations, setTranslations] = useState();

  useEffect(() => {
    console.log("Fetching translations");
    setTranslations({ foobar: "BAZ" });
  }, []);

  return (
    <Provider store={store}>
      <IntlProvider locale="en" messages={translations}>
        <LineList />
      </IntlProvider>
    </Provider>
  );
}

const CURRENT_PROJECT_ID = window.project.id;
store.dispatch(actions.initialize({ id: CURRENT_PROJECT_ID }));
