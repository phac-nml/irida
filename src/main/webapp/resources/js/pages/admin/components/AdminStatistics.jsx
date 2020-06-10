
import { Layout, Menu } from "antd";
import { grey1 } from "../../../styles/colors";
import { Link, Location, Router } from "@reach/router";
import { SETTINGS } from "../../analysis/routes";
import React, { Suspense } from "react";
const { SubMenu } = Menu;

import { TabPaneContent } from "../../../components/tabs";

export default function AdminStatistics() {
  return (
    <TabPaneContent title={i18n("admin.panel.statistics")}>
    </TabPaneContent>
  );
}