import React from "react";
import { createRoot } from 'react-dom/client';
import { PageWrapper } from "../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { UserTable } from "./UserTable";

import store from "../store";
import { Provider } from "react-redux";
import CreateNewUser from "../../admin/components/user/CreateNewUser";

/**
 * React component to display the users table for managers.
 * @returns {*}
 * @constructor
 */
export function UserListPage() {
  return (
    <Provider store={store}>
      <PagedTableProvider url={setBaseUrl("ajax/users/list")}>
        <PageWrapper
          title={i18n("UserListPage.title")}
          headerExtras={<CreateNewUser />}
        >
          <UserTable />
        </PageWrapper>
      </PagedTableProvider>
    </Provider>
  );
}

const container = document.getElementById('react-root');
const root = createRoot(container);
root.render(<UserListPage />);
