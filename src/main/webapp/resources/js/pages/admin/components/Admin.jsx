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
const UsersPage = lazy(() => import("../../UsersPage"));
const AdminUserGroupsPage = lazy(() => import("../../UserGroupsPage/components/UserGroupsPage"));

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
                <UsersPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.USERS}`
                  }
                />
                <AdminUserGroupsPage
                  path={
                    `${DEFAULT_URL}/${ADMIN.USERGROUPS}`
                  }
                />
              </Router>
            </Suspense>
          </Content>
        </Layout>
    </Layout>
  );
}