import React from "react";
import { render } from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { PageWrapper } from "../components/page/PageWrapper";
import { PagedTableProvider } from "../components/ant.design/PagedTable";
import { setBaseUrl } from "../utilities/url-utilities";
import { AddNewButton } from "../components/Buttons/AddNewButton";
import { UsersTable } from "../components/UsersTable/UsersTable";

import store from "../pages/user/store";
import { Provider } from "react-redux";
import { UserAccountApp } from "./user/components/UserAccountApp";

function UsersTableLayout() {
  return (
    <PageWrapper
      title={i18n("UsersPage.title")}
      headerExtras={
        <AddNewButton
          href={setBaseUrl(`users/create`)}
          text={i18n("UsersPage.add")}
        />
      }
    >
      <PagedTableProvider url={setBaseUrl("ajax/users/list")}>
        <UsersTable />
      </PagedTableProvider>
    </PageWrapper>
  );
}

/**
 * React component to display the page for administration of users.
 * @returns {*}
 * @constructor
 */
export function UsersPage() {
  return (
    <Provider store={store}>
      <BrowserRouter basename={setBaseUrl(`/users`)}>
        <Routes>
          <Route index element={<UsersTableLayout />} />
          <Route path=":userId" element={<UserAccountApp />} />
        </Routes>
      </BrowserRouter>
    </Provider>
  );
}

render(<UsersPage />, document.querySelector("#react-root"));
