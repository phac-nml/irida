import React, { useState } from "react";
import { Layout, Tabs } from "antd";
import { grey1 } from "../../../styles/colors";
import { MetadataTemplateProvider } from "../../../contexts/metadata-template-context";
import TemplateDetails from "./details";
import TemplateFields from "./fields";

const { Content } = Layout;

/**
 * React component to render the layout for the metadata template details
 * page.
 * @param {JSX.element} children
 * @param {number} templateId - Identifier for the current metadata template
 * @returns {JSX.Element}
 * @constructor
 */
export default function MetadataTemplatePage({ children, templateId }) {
  const [activeKey, setActiveKey] = useState("details");

  return (
    <MetadataTemplateProvider id={templateId}>
      <Layout>
        <Content
          style={{
            backgroundColor: grey1,
          }}
        >
          <Tabs onChange={setActiveKey} activeKey={activeKey}>
            <Tabs.TabPane key="details" tab={i18n("TemplatePage.menu.details")}>
              <div style={{ padding: 15 }}>
                <TemplateDetails />
              </div>
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
