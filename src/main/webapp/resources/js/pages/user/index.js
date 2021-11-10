import { render } from "react-dom";
import React from "react";
import { Router } from "@reach/router";
import { Provider } from "react-redux";
import { Col, Menu, Row } from 'antd';
import { PageWrapper } from "../../components/page/PageWrapper";

import store from "./store";
import UserAccountNav from "./components/UserAccountNav";
import UserDetailsPage from "./components/UserDetailsPage";
import UserGroupsPage from "./components/UserGroupsPage";
import UserProjectsPage from "./components/UserProjectsPage";
import UserPasswordPage from "./components/UserPasswordPage";

render(
  <Provider store={store}>
    <PageWrapper title="User Account">
      <Row>
        <Col span={5}>
          <UserAccountNav />
        </Col>
        <Col span={1} />
        <Col span={18}>
          <Router>
            <UserDetailsPage default path="details" />
            <UserGroupsPage path="groups"/>
            <UserProjectsPage path="projects" />
            <UserPasswordPage path="password" />
          </Router>
        </Col>
      </Row>
    </PageWrapper>
  </Provider>,
  document.querySelector("#user-account-root")
);
