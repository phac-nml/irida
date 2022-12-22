import { Layout } from "antd";
import MainNavigation from "../components/main-navigation";
import React, { Suspense } from "react";
import { LoadingOutlined } from "@ant-design/icons";
import { Outlet } from "react-router-dom";

/**
 * Global layout component
 * @constructor
 */
const AppLayout = (): JSX.Element => (
  <Layout style={{ height: `100vh` }}>
    <Layout.Header>
      <MainNavigation />
    </Layout.Header>
    <Layout.Content style={{ overflowY: "auto" }}>
      <Suspense fallback={<LoadingOutlined />}>
        <Outlet />
      </Suspense>
    </Layout.Content>
  </Layout>
);

export default AppLayout;
