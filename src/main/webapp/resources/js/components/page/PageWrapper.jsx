import React from "react";
import PropTypes from "prop-types";
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
 * @returns {*}
 * @constructor
 */
export function PageWrapper({ title, headerExtras, children }) {
  return (
    <Layout style={{ height: "100%", minHeight: "100%" }}>
      <PageHeader title={title} extra={headerExtras} />
      <Content style={{ padding: SPACE_MD }}>
        <div style={{ padding: SPACE_MD, backgroundColor: grey1 }}>
          {children}
        </div>
      </Content>
    </Layout>
  );
}

PageWrapper.propTypes = {
  title: PropTypes.string.isRequired,
  headerExtras: PropTypes.element,
  children: PropTypes.element.isRequired
};
