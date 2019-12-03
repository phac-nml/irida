/**
 * Component to render a div for tabular output
 */

import React from "react";
import PropTypes from "prop-types";
import { SPACE_XS } from "../../styles/spacing";
import styled from "styled-components";

/**
 * Stateless UI component for displaying a div for tabular output
 *
 * @param {number} height - Height of div. Default is 300px
 * @param {object} children - content to display in div
 * @param {object} props - any other attributes to add to TabularOutputWrapper
 *
 * @returns {Element} - Returns a div component for tabular output
 */

export function TabularOutputWrapper({ height = 300, children, ...props }) {
  const Wrapper = styled.div`
    height: ${height}px;
    width: 100%;
    margin-bottom: ${SPACE_XS};
  `;

  return (
    <Wrapper style={{ height: height }} {...props}>
      {children}
    </Wrapper>
  );
}

TabularOutputWrapper.propTypes = {
  /*Optional height*/
  height: PropTypes.number,
  /*the content to display in the div*/
  children: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),
  /*any extra attributes to add to JsonOutputWrapper*/
  props: PropTypes.object
};
