import React, { useState } from "react";
import { Layout, Menu } from "antd";
import Sider from "antd/es/layout/Sider";
import { Link, Router } from "@reach/router";
import { grey1 } from "../../../styles/colors";
import { MetadataTemplateProvider } from "../../../contexts/metadata-template-context";
import { TemplateFields } from "./TemplateFields";
import { TemplateDetails } from "./TemplateDetails";
import { parseMetadataTemplateUrl } from "./template-utilities";

const { Content } = Layout;

function PageLayout({ baseUrl, path }) {
  const [activeKey, setActiveKey] = useState(path);
  console.log(activeKey);

  return (
    <Layout>
      <Sider width={200}>
        <Menu
          onClick={(event) => setActiveKey(event.key)}
          mode="inline"
          style={{ height: "100%", borderRight: 0, width: 200 }}
          selectedKeys={[activeKey]}
        >
          <Menu.Item key="details">
            <Link to={baseUrl}>{i18n("TemplatePage.menu.details")}</Link>
          </Menu.Item>
          <Menu.Item key="fields">
            <Link to={`${baseUrl}/fields`}>
              {i18n("TemplatePage.menu.fields")}
            </Link>
          </Menu.Item>
        </Menu>
      </Sider>
      <Content
        style={{
          backgroundColor: grey1,
          paddingLeft: 20,
        }}
      >
        <Router>
          <TemplateDetails path={baseUrl} />
          <TemplateFields path={`${baseUrl}/fields`} />
        </Router>
      </Content>
    </Layout>
  );
}

export function TemplatePage() {
  /*
  Use the current href to determine which template we are looking at and
  to establish the base url for this mini-application.
   */
  let [url, templateId, path] = parseMetadataTemplateUrl(window.location.href);

  return (
    <MetadataTemplateProvider id={templateId}>
      <PageLayout baseUrl={url} path={path} />
    </MetadataTemplateProvider>
  );
}
