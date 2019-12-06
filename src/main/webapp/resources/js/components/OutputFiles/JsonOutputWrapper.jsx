/**
 * Component to render a div for JSON output
 */

import React from "react";
import PropTypes from "prop-types";
import { SPACE_XS } from "../../styles/spacing";
import styled from "styled-components";
import { DIV_BORDER } from "../../styles/borders";

/**
 * Stateless UI component for displaying a div for json output
 *
 * @param {number} height - Height of div. Default is 300px
 * @param {bool} overflowRequired - Optional overflow for div
 * @param {object} children - content to display in div
 * @param {object} props - any other attributes to add to JsonOutputWrapper
 *
 * @returns {Element} - Returns a scrollable div component
 */

export function JsonOutputWrapper({
  height = 300,
  overflowRequired = false,
  children,
  ...props
}) {
  const overflow = overflowRequired ? "auto" : "none";

  const Wrapper = styled.div`
    height: ${height}px;
    margin-bottom: ${SPACE_XS};
    border: ${DIV_BORDER};
    padding: 0 0 2px ${SPACE_XS};
    white-space: pre;
    overflow: ${overflow};
  `;

  return (
    <Wrapper style={{ height: height }} {...props}>
      {children}
    </Wrapper>
  );
}

JsonOutputWrapper.propTypes = {
  /*Optional height*/
  height: PropTypes.number,
  /*Optional overflowRequired*/
  overflowRequired: PropTypes.bool,
  /*the content to display in the div*/
  children: PropTypes.oneOfType([
    PropTypes.object,
    PropTypes.array,
    PropTypes.string
  ]),
  /*any extra attributes to add to JsonOutputWrapper*/
  props: PropTypes.object
};
