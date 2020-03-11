import React from "react";
import { render } from "react-dom";
import { Layout } from "antd";
import { Session } from "../session/Session";
import { Notifications } from "../notifications/Notifications";
import GalaxyAlert from "./GalaxyAlert";
import { MainNavigation } from "../nav/MainNavigation";
import { Breadcrumbs } from "./Breadcrumbs";

const { Content, Header } = Layout;

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = `dist/`;

export function PageHeader({ children }) {
  return (
    <Layout>
      <MainNavigation />
      <Breadcrumbs crumbs={window.breadcrumbs || []} />
      <Session />
      <Notifications />
      <GalaxyAlert />
      <Content>{children}</Content>
    </Layout>
  );
}

render(<PageHeader />, document.querySelector(".js-page-header"));
