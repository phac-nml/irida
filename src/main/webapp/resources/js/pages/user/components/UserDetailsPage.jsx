import React, { useState } from "react";
import { Col, Menu, Row } from 'antd';
import { PageWrapper } from "../../../components/page/PageWrapper";

/**
 * React component to display the user details page.
 * @returns {*}
 * @constructor
 */
export function UserDetailsPage({}) {
  const [content, setContent] = useState("account");

  return (
  <PageWrapper title="User Details">
    <Row>
      <Col flex="300px">
        <Menu
          mode="inline"
          selectedKeys={[content]}
        >
          <Menu.Item key="account" onClick={() => setContent("account")}>Account</Menu.Item>
          <Menu.Item key="groups" onClick={() => setContent("groups")}>Groups</Menu.Item>
          <Menu.Item key="projects" onClick={() => setContent("projects")}>Projects</Menu.Item>
          <Menu.ItemGroup key="security" title="Security">
            <Menu.Item key="password" onClick={() => setContent("password")}>Password Reset</Menu.Item>
          </Menu.ItemGroup>
        </Menu>
      </Col>
      <Col flex="auto">
        <div id="content"  style={{ paddingLeft: 40}}>
          {content === "account" && (<div>Account</div>)}
          {content === "groups" && (<div>Groups</div>)}
          {content === "projects" && (<div>Organization</div>)}
          {content === "password" && (<div>Password Reset</div>)}
        </div>
      </Col>
    </Row>
  </PageWrapper>
  );
}
