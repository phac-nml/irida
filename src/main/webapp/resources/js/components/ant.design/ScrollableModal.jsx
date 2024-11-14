/**
 * @file ScrollableModal is a styled component for ant.design modal that has scrollable content.
 */

import React from "react";
import styled from "styled-components";
import { Modal } from "antd";

const ScrollBodyModal = styled(Modal)`
  .ant-modal-body {
    padding: 15px 25px;
    overflow-y: auto;
    max-height: ${(props) => (props.maxHeight ? `${props.maxHeight}px` : `600px`)};
  }
`;

/**
 * React component to be used when you want a scrollable content in your ant design modal.
 *
 * @param {string} maxHeight - maximum height of the scrollable body
 * @param {element} children - the modal content
 * @returns {*}
 * @constructor
 */
export function ScrollableModal({ maxHeight = 600, children, ...props }) {
  return (
    <ScrollBodyModal maxHeight={maxHeight} {...props}>
      {children}
    </ScrollBodyModal>
  );
}
