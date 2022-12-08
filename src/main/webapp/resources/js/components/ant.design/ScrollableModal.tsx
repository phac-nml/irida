/**
 * @file ScrollableModal is a styled component for ant.design modal that has scrollable content.
 */

import React from "react";
import type { ModalProps } from "antd";
import { Modal } from "antd";
import styled from "styled-components";

const ScrollBodyModal = styled(Modal)<{ maxHeight: number }>`
  .ant-modal-body {
    padding: 15px 25px;
    overflow-y: auto;
    max-height: ${(props) => `${props.maxHeight}px` || `600px`};
  }
`;

type ScrollableModalProps = ModalProps & {
  maxHeight?: number;
  children: JSX.Element;
};

/**
 * React component to be used when you want a scrollable content in your ant design modal.
 *
 * @param maxHeight - maximum height of the scrollable body
 * @param children - the modal content
 * @param props - modal props
 */
export function ScrollableModal({
  maxHeight = 600,
  children,
  ...props
}: ScrollableModalProps): JSX.Element {
  return (
    <ScrollBodyModal maxHeight={maxHeight} {...props}>
      {children}
    </ScrollBodyModal>
  );
}
