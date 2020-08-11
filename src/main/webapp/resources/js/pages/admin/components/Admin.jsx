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
import React, { Suspense, lazy} from "react";
import { ContentLoading } from "../../../components/loader";

const { Content } = Layout;

import AdminHeader from "./AdminHeader";
import AdminSideMenu from "./AdminSideMenu"
import { setBaseUrl } from "../../../utilities/url-utilities";

const AdminStatistics = lazy(() => import("./AdminStatistics"));
const AdminUsersPage = lazy(() => import("./AdminUsersPage"));
const AdminUserGroupsPage = lazy(() => import("./AdminUserGroupsPage"));
const ClientListingPage = lazy(() => import("./clients/listing/ClientListingPage"));
const AdminRemoteApiPage = lazy(() => import("./AdminRemoteApiPage"));
const AdminSequencingRunsPage = lazy(() => import("./AdminSequencingRunsPage"));
const AnnouncementAdminPage = lazy(() => import("./announcements/AnnouncementAdminPage"));
const AdminNcbiExportsPage = lazy(() => import("./ncbi-exports/AdminNcbiExportsPage"));

export default function Admin() {
  const DEFAULT_URL = setBaseUrl("/admin");

  /*
   * The following renders the tabs for statistics, users, and groups
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
                <AdminStatistics
                  path={
                    `${DEFAULT_URL}/${ADMIN.STATISTICS}`
                  }
                  default
                />
                <AdminUsersPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.USERS}`
                  }
                />
                <AdminUserGroupsPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.USERGROUPS}/*`
                  }
                />
                <ClientListingPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.CLIENTS}`
                  }
                />
                <AdminRemoteApiPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.REMOTEAPI}`
                  }
                />
                <AdminSequencingRunsPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.SEQUENCINGRUNS}`
                  }
                />
                <AdminNcbiExportsPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.NCBIEXPORTS}`
                  }
                />
                <AnnouncementAdminPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.ANNOUNCEMENTS}`
                  }
                />
              </Router>
            </Suspense>
          </Content>
        </Layout>
    </Layout>
  );
}