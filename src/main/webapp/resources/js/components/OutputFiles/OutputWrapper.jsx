/**
 * Component to render a div for output
 */

import React from "react";
import { SPACE_XS } from "../../styles/spacing";
import styled from "styled-components";
import { BORDERED_LIGHT } from "../../styles/borders";

/**
 * Stateless UI component for displaying a div for output
 *
 * @param {number} height - Height of div. Default is 300px
 * @param {bool} overflowRequired - Optional overflow for div
 * @param {object} children - content to display in div
 * @param {object} props - any other attributes to add to OutputWrapper
 *
 * @returns {Element} - Returns a scrollable div component
 */

export function OutputWrapper({
  height = 300,
  overflowRequired = false,
  children,
  ...props
}) {
  const overflow = overflowRequired ? "auto" : "none";

  const Wrapper = styled.div`
    height: ${height}px;
    margin-bottom: ${SPACE_XS};
    border: ${BORDERED_LIGHT};
    padding: 0 0 2px ${SPACE_XS};
    white-space: pre-wrap;
    overflow: ${overflow};
  `;

  return (
    <Wrapper style={{ height: height }} {...props}>
      {children}
    </Wrapper>
  );
}
