import React from "react";
import { Layout, PageHeader } from "antd";
import { grey1 } from "../../styles/colors";
import { SPACE_MD } from "../../styles/spacing";

const { Content } = Layout;

/**
 * Standard page layout for pages that do not required full width layouts (e.g. cart)
 * @param {string} title The page title.
 * @param {element} headerExtras Extra information (buttons, search fields, to display
 * in the page header.
 * @param {element} children Main content for the page.
 * @param {function} onBack - function to handle on clicking back button
 * @param {string} subTitle
 * @returns {*}
 * @constructor
 */
export function PageWrapper({
  title,
  headerExtras,
  children,
  onBack = undefined,
  subTitle = "",
}) {
  return (
    <Layout style={{ height: "100%", minHeight: "100%" }}>
      <PageHeader
        className="t-main-heading"
        title={title}
        subTitle={subTitle}
        extra={headerExtras}
        onBack={onBack}
      />
      <Content style={{ padding: SPACE_MD, paddingTop: 0 }}>
        <div style={{ padding: SPACE_MD, backgroundColor: grey1 }}>
          {children}
        </div>
      </Content>
    </Layout>
  );
}