import React, { useState } from "react";
import { Layout, Tabs } from "antd";
import { grey1 } from "../../../styles/colors";
import { MetadataTemplateProvider } from "../../../contexts/metadata-template-context";
import { TemplateDetails } from "./TemplateDetails";
import { TemplateFields } from "./TemplateFields";

const { Content } = Layout;

export function TemplatePage({ children, templateId }) {
  const [activeKey, setActiveKey] = useState("details");

  return (
    <MetadataTemplateProvider id={templateId}>
      <Layout>
        <Content
          style={{
            backgroundColor: grey1,
            paddingLeft: 20,
          }}
        >
          <Tabs onChange={setActiveKey} activeKey={activeKey}>
            <Tabs.TabPane key="details" tab={i18n("TemplatePage.menu.details")}>
              <TemplateDetails />
            </Tabs.TabPane>
            <Tabs.TabPane key="fields" tab={i18n("TemplatePage.menu.fields")}>
              <TemplateFields />
            </Tabs.TabPane>
          </Tabs>
          {children}
        </Content>
      </Layout>
    </MetadataTemplateProvider>
  );
}
