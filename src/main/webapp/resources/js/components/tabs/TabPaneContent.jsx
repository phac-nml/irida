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
 * @param {number} colSpan - the number of columns for the layout
 * @param {object} children - content to display in tab
 * @param {object} props - any other attributes to add to PageHeader
 *
 * @returns {Element} - Returns a component which displays the tab title and content
 */

export function TabPaneContent({ title, colSpan = 12, children, ...props }) {
  return (
    <Col span={colSpan}>
      <PageHeader
        style={{ padding: 0, paddingBottom: SPACE_MD }}
        title={title}
        {...props}
      />
      {children}
    </Col>
  );
}

TabPaneContent.propTypes = {
  /*title to display in tab*/
  title: PropTypes.string,
  /*number of columns (width)*/
  colSpan: PropTypes.number,
  /*the content to display in the tab*/
  children: PropTypes.object.isRequired,
  /*any extra attributes to add to PageHeader*/
  props: PropTypes.object
};
