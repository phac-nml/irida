import React from "react";
import { render } from "react-dom";
import { Router } from "@reach/router";
import { Layout } from "antd";
const ProjectDetails = React.lazy(() => import("./components/ProjectDetails"));

const { Content, Sider } = Layout;

const Root = ({ children }) => children;

const SettingsLayout = () => (
  <Layout>
    <Sider>SIDER</Sider>
    <Layout>
      <Content>
        <Router>
          <ProjectDetails path="details" />
        </Router>
      </Content>
    </Layout>
  </Layout>
);

const ProjectSettings = () => (<Router>
  <Root path="/projects/:projectId/settings/*">
    <SettingsLayout
  </Root>
</Router>)

render(<ProjectSettings />, document.querySelector("#root"));
