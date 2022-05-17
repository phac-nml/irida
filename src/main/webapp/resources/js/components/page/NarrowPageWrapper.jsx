import React from "react";
import { Col, Layout, PageHeader, Row } from "antd";
import "./styles.css";

/**
 * Standard page layout for pages that have a narrow width layout (e.g. user account details)
 *
 * @param {string} title - page title
 * @param {element} headerExtras - extra information (buttons, search fields, etc.) to display in the page header
 * @param {function} onBack - function to handle on clicking back button
 * @param {string} subTitle - page subtitle
 * @param {element} children - main content for the page
 * @returns {*}
 * @constructor
 */
export function NarrowPageWrapper({
  title,
  headerExtras,
  onBack = undefined,
  subTitle = "",
  children,
}) {
  return (
    <Layout style={{ height: "100%", minHeight: "100%" }}>
      <Row>
        <Col
          xxl={{ span: 12, offset: 6 }}
          xl={{ span: 20, offset: 2 }}
          sm={{ span: 22, offset: 1 }}
        >
          <PageHeader
            className="t-main-heading"
            title={title}
            subTitle={subTitle}
            extra={headerExtras}
            onBack={onBack}
          />
          {children}
        </Col>
      </Row>
    </Layout>
  );
}
