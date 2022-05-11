/*
 * This file renders the details for the admin panel as well as,
 * lazily loads the Users, User Groups, and Statistics components (component
 * is only loaded when the corresponding tab is clicked)
 */

/*
 * The following import statements makes available all the elements
 * required by the components encompassed within
 */

import { Layout } from "antd";
import React, { lazy, Suspense } from "react";
import { Route, Routes } from "react-router-dom";
import { ContentLoading } from "../../../components/loader";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ADMIN } from "../routes";
import { AdminContent } from "./AdminContent";
import AdminHeader from "./AdminHeader";
import AdminSideMenu from "./AdminSideMenu";

import store from '../../user/store'
import { Provider } from 'react-redux'

const { Content } = Layout;

const AdvancedStatistics = lazy(() =>
  import("./statistics/AdvancedStatistics")
);
const BasicStats = lazy(() => import("./statistics/BasicStats"));

const AdminUsersPage = lazy(() => import("./AdminUsersPage"));
const UserGroupsPage = lazy(() =>
  import("../../UserGroupsPage/components/UserGroupsPage")
);
const UserGroupsDetailsPage = lazy(() =>
  import("../../UserGroupsPage/components/UserGroupDetailsPage")
);
const ClientListingPage = lazy(() =>
  import("./clients/listing/ClientListingPage")
);
const AdminRemoteApiPage = lazy(() =>
  import("./remote-connections/AdminRemoteApiPage")
);
const AdminSequencingRunsPage = lazy(() => import("./AdminSequencingRunsPage"));
const AnnouncementAdminPage = lazy(() =>
  import("./announcements/AnnouncementAdminPage")
);
const AdminNcbiExportsPage = lazy(() =>
  import("./ncbi-exports/AdminNcbiExportsPage")
);

export default function Admin() {
  const DEFAULT_URL = setBaseUrl("/admin");

  /*
   * The following renders the tabs for the admin panel
   * the components are only loaded if the corresponding tab is clicked
   */
  return (
  <Provider store={store}>
    <Layout>
      <AdminSideMenu />
      <Layout>
        <AdminHeader />
        <Content>
          <Suspense fallback={<ContentLoading />}>
            <Routes>
              <Route path={DEFAULT_URL} element={<AdminContent />}>
                <Route index element={<BasicStats />} />
                <Route
                  path={`${ADMIN.STATISTICS}/:statType`}
                  element={<AdvancedStatistics />}
                />
                <Route path={ADMIN.USERS} element={<AdminUsersPage />} />

                <Route
                  path={`${ADMIN.USERGROUPS}/list`}
                  element={
                    <UserGroupsPage
                      baseUrl={`${DEFAULT_URL}/${ADMIN.USERGROUPS}`}
                    />
                  }
                />
                <Route
                  path={`${ADMIN.USERGROUPS}/:id`}
                  element={<UserGroupsDetailsPage />}
                />
                <Route path={ADMIN.CLIENTS} element={<ClientListingPage />} />
                <Route
                  path={ADMIN.REMOTEAPI}
                  element={<AdminRemoteApiPage />}
                />
                <Route
                  path={ADMIN.SEQUENCINGRUNS}
                  element={<AdminSequencingRunsPage />}
                />
                <Route
                  path={ADMIN.NCBIEXPORTS}
                  element={<AdminNcbiExportsPage />}
                />
                <Route
                  path={ADMIN.ANNOUNCEMENTS}
                  element={<AnnouncementAdminPage />}
                />
              </Route>
            </Routes>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  </Provider>
  );
}
