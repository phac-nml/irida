import React from "react";
import styled from "styled-components";
import { Modal } from "antd";

const ScrollBodyModal = styled(Modal)`
  .ant-modal-body {
    padding: 15px 25px;
    overflow-y: auto;
    max-height: ${(props) => `${props.maxHeight}px` || `600px`};
  }
`;

export function ScrollableModal({ maxHeight = 600, children, ...props }) {
  return (
    <ScrollBodyModal maxHeight={maxHeight} {...props}>
      {children}
    </ScrollBodyModal>
  );
}
