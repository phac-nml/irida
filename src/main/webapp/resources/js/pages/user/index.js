import { render } from "react-dom";
import React from "react";
import { Router } from "@reach/router";
import { Provider } from "react-redux";
import { Col, Menu, Row } from 'antd';
import { PageWrapper } from "../../components/page/PageWrapper";

import store from "./store";
import UserDetailsNav from "./components/UserDetailsNav";
import UserAccountPage from "./components/UserAccountPage";
import UserGroupsPage from "./components/UserGroupsPage";
import UserProjectsPage from "./components/UserProjectsPage";
import UserPasswordPage from "./components/UserPasswordPage";

render(
  <Provider store={store}>
    <PageWrapper title="User Details">
      <Row>
        <Col flex="300px">
          <UserDetailsNav />
        </Col>
        <Col flex="auto">
          <Router>
            <UserAccountPage path="account" />
            <UserGroupsPage path="groups"/>
            <UserProjectsPage path="projects" />
            <UserPasswordPage path="password" />
          </Router>
        </Col>
      </Row>
    </PageWrapper>
  </Provider>,
  document.querySelector("#user-details-root")
);
