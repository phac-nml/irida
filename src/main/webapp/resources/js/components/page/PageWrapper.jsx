import React from "react";
import PropTypes from "prop-types";
import { Layout, PageHeader } from "antd";
import { grey1 } from "../../styles/colors";

const { Content } = Layout;

/**
 * Standard page layout for pages that do not required full width layouts (e.g. cart)
 * @param {string} title The page title.
 * @param {element} headerExtras Extra information (buttons, search fields, to display
 * in the page header.
 * @param {element} children Main content for the page.
 * @returns {*}
 * @constructor
 */
export function PageWrapper({ title, headerExtras, children }) {
  return (
    <Layout style={{ padding: 24, height: "100%", minHeight: "100%" }}>
      <Content style={{ backgroundColor: grey1, margin: 0 }}>
        <PageHeader title={title} extra={headerExtras} />
        {children}
      </Content>
    </Layout>
  );
}

PageWrapper.propTypes = {
  title: PropTypes.string.isRequired,
  headerExtras: PropTypes.element,
  children: PropTypes.element.isRequired
};
