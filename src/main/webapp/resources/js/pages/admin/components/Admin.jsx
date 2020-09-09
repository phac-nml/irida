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
import { Router } from "@reach/router";
import { ADMIN } from "../routes";
import React, { lazy, Suspense } from "react";
import { ContentLoading } from "../../../components/loader";
import AdminHeader from "./AdminHeader";
import AdminSideMenu from "./AdminSideMenu";
import { setBaseUrl } from "../../../utilities/url-utilities";

const { Content } = Layout;

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
            <Router>
              <AdminUsersPage path={`${DEFAULT_URL}/${ADMIN.USERS}`} default />
              <AdminUserGroupsPage
                path={`${DEFAULT_URL}/${ADMIN.USERGROUPS}/*`}
              />
              <ClientListingPage path={`${DEFAULT_URL}/${ADMIN.CLIENTS}`} />
              <AdminRemoteApiPage path={`${DEFAULT_URL}/${ADMIN.REMOTEAPI}`} />
              <AdminRemoteApiDetailsPage
                path={`${DEFAULT_URL}/${ADMIN.REMOTEAPI}/:remoteId`}
              />
              <AdminSequencingRunsPage
                path={`${DEFAULT_URL}/${ADMIN.SEQUENCINGRUNS}`}
              />
              <AdminNcbiExportsPage
                path={`${DEFAULT_URL}/${ADMIN.NCBIEXPORTS}`}
              />
              <AnnouncementAdminPage
                path={`${DEFAULT_URL}/${ADMIN.ANNOUNCEMENTS}`}
              />
            </Router>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
}
