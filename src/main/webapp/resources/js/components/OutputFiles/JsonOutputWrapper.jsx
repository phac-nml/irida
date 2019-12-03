/**
 * Component to render a div for JSON output
 */

import React from "react";
import PropTypes from "prop-types";
import { SPACE_XS } from "../../styles/spacing";
import { grey4 } from "../../styles/colors";
import styled from "styled-components";

/**
 * Stateless UI component for displaying a div for json output
 *
 * @param {number} height - Height of div. Default is 300px
 * @param {string} description - Optional description
 * @param {object} children - content to display in div
 * @param {object} props - any other attributes to add to JsonOutputWrapper
 *
 * @returns {Element} - Returns a scrollable div component
 */

export function JsonOutputWrapper({ height = 300, children, ...props }) {
  const Wrapper = styled.div`
    height: ${height}px;
    width: 100%;
    overflow: auto;
    margin-bottom: ${SPACE_XS};
    border: solid 1px ${grey4};
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
  /*the content to display in the div*/
  children: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),
  /*any extra attributes to add to JsonOutputWrapper*/
  props: PropTypes.object
};
