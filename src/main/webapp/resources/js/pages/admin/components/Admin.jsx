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
import AdminHeader from "./AdminHeader";
import AdminSideMenu from "./AdminSideMenu";

const { Content } = Layout;

const AdvancedStatistics = lazy(() =>
  import("./statistics/AdvancedStatistics")
);
const BasicStats = lazy(() => import("./statistics/BasicStats"));

const AdminUsersPage = lazy(() => import("./AdminUsersPage"));
const AdminUserGroupsPage = lazy(() => import("./AdminUserGroupsPage"));
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
const AdminRemoteApiDetailsPage = lazy(() =>
  import("./remote-connections/RemoteConnectionDetails")
);

export default function Admin() {
  const DEFAULT_URL = setBaseUrl("/admin");

  /*
   * The following renders the tabs for the admin panel
   * the components are only loaded if the corresponding tab is clicked
   */
  return (
    <Layout>
      <AdminSideMenu />
      <Layout>
        <AdminHeader />
        <Content>
          <Suspense fallback={<ContentLoading />}>
            <Routes>
              <Route
                path={`${DEFAULT_URL}/${ADMIN.STATISTICS}`}
                element={<BasicStats />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.STATISTICS}/:statType`}
                element={<AdvancedStatistics />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.USERS}`}
                element={<AdminUsersPage />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.USERGROUPS}/*`}
                element={<AdminUserGroupsPage />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.CLIENTS}`}
                element={<ClientListingPage />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.REMOTE_API}`}
                element={<AdminRemoteApiPage />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.REMOTEAPI}/:remoteId`}
                element={<AdminRemoteApiDetailsPage />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.SEQUENCING_RUNS}`}
                element={<AdminSequencingRunsPage />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.NCBIEXPORTS}`}
                element={<AdminNcbiExportsPage />}
              />
              <Route
                path={`${DEFAULT_URL}/${ADMIN.ANNOUNCEMENTS}`}
                element={<AnnouncementAdminPage />}
              />
            </Routes>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
}
