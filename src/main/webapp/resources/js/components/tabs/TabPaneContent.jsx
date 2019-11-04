/**
 * Component to render proper layout for a tab.
 * Use this to display content in a tab pane.
 */

import React from "react";
import PropTypes from "prop-types";
import { Col, PageHeader } from "antd";

import { SPACE_MD } from "../../styles/spacing";

/**
 * Stateless UI component for creating vertical tabs
 * @param {string} title - title for tab
 * @param {object} children - content to display in tab
 *
 * @returns {Element} - Returns a component which displays the tab title and content
 */

export function TabPaneContent({ title, children }) {
  return (
    <Col span={12}>
      <PageHeader
        style={{ padding: 0, paddingBottom: SPACE_MD }}
        title={title}
      />
      {children}
    </Col>
  );
}

TabPaneContent.propTypes = {
  /*title to display in tab*/
  title: PropTypes.string,
  /*the content to display in the tab*/
  children: PropTypes.object.isRequired
};
