
import { Layout, Menu } from "antd";
import { grey1 } from "../../../styles/colors";
import { Link, Location, Router } from "@reach/router";
import { SETTINGS } from "../../analysis/routes";
import { SPACE_MD } from "../../../styles/spacing";
import React, { Suspense } from "react";
import { ContentLoading } from "../../../components/loader";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ADMIN } from "../routes";
import { TabPaneContent } from "../../../components/tabs";

const AdminUsersPage = lazy(() => import("../../AdminUsersPage"));

export default function AdminUsers() {
  const pathRegx = new RegExp(/([a-zA-Z]+)$/);

  const DEFAULT_URL = `/admin/` + setBaseUrl(ADMIN.USERS);

  return (
    <TabPaneContent title={i18n("admin.panel.users")}>
      <AdminUsersPage />
    </TabPaneContent>
  );
}