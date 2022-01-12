import React from "react";
import { render } from "react-dom";
import { PageWrapper } from "../components/page/PageWrapper";
import { PagedTableProvider } from "../components/ant.design/PagedTable";
import { setBaseUrl } from "../utilities/url-utilities";
import { AddNewButton } from "../components/Buttons/AddNewButton";
import { UsersTable } from "../components/UsersTable/UsersTable";

import store from '../pages/user/store'
import { Provider } from 'react-redux'

/**
 * React component to display the page for administration of users.
 * @returns {*}
 * @constructor
 */
export function UsersPage() {
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
        <Provider store={store}>
          <UsersTable />
        </Provider>
      </PagedTableProvider>
    </PageWrapper>
  );
}

render(<UsersPage />, document.querySelector("#react-root"));