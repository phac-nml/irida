import React, { useState } from "react";
import { Layout, Menu } from "antd";
import Sider from "antd/es/layout/Sider";
import { Link, useLocation } from "@reach/router";
import { grey1 } from "../../../styles/colors";
import { MetadataTemplateProvider } from "../../../contexts/metadata-template-context";

const { Content } = Layout;

export function TemplatePage({ children, templateId }) {
  const [activeKey, setActiveKey] = useState("details");

  const location = useLocation();

  return (
    <MetadataTemplateProvider id={templateId}>
      <Layout>
        <Sider width={200}>
          <Menu
            onClick={(event) => setActiveKey(event.key)}
            mode="inline"
            style={{ height: "100%", borderRight: 0, width: 200 }}
            selectedKeys={[activeKey]}
          >
            <Menu.Item key="details">
              <Link to={""}>{i18n("TemplatePage.menu.details")}</Link>
            </Menu.Item>
            <Menu.Item key="fields">
              <Link to={`${location.pathname}/fields`}>
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
          {children}
        </Content>
      </Layout>
    </MetadataTemplateProvider>
  );
}
