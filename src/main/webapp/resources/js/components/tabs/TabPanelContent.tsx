/**
 * Component to render proper layout for a tab.
 * Use this to display content in a tab pane.
 */

import React from "react";
import { Col, PageHeader, PageHeaderProps } from "antd";

import { SPACE_MD } from "../../styles/spacing";
import styled from "styled-components";

type TabPanelContentProps = {
  title: string;
  actionButton?: JSX.Element;
  xs: number;
  xl?: number;
  xxl?: number;
  children: JSX.Element;
  props?: Partial<PageHeaderProps>;
};

const Column = styled(Col)`
  h2 {
    font-size: 16px;
  }
`;

/**
 * Stateless UI component for creating vertical tabs
 * @param {string} title - title for tab
 * @param {Element} actionButton - button lined up horizontally with title
 * @param {number} xs - width of container for screen <576px
 * @param {number} xl - width of container for screen >=1200px
 * @param {number} xxl - width of container for screen >=1600px
 * @param {object} children - content to display in tab
 * @param {object} props - any other attributes to add to PageHeader
 *
 * @returns {Element} - Returns a component which displays the tab title and content
 */

export function TabPanelContent({
  title,
  actionButton,
  xs = 24,
  xl = 18,
  xxl = 12,
  children,
  ...props
}: TabPanelContentProps) {
  return (
    <Col
      xs={xs}
      xl={xl}
      xxl={xxl}
      style={{
        height: `100%`,
      }}
    >
      <PageHeader
        style={{
          padding: 0,
          paddingBottom: SPACE_MD,
        }}
        title={title}
        extra={actionButton}
        {...props}
      />
      {children}
    </Col>
  );
}
