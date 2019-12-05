/**
 * Component to render a div for JSON object output
 */

import React from "react";
import { Typography } from "antd";
import PropTypes from "prop-types";
import { SPACE_XS } from "../../styles/spacing";
import { grey1 } from "../../styles/colors";
import styled from "styled-components";
import { border1 } from "../../styles/borders";

const { Text } = Typography;

/**
 * Stateless UI component for displaying a div for json object output
 *
 * @param {number} height - Height of div. Default is 300px
 * @param {string} description - Optional description
 * @param {object} children - content to display in div
 * @param {object} props - any other attributes to add to JsonOutputWrapper
 *
 * @returns {Element} - Returns a pre component
 */

export function JsonObjectOutputWrapper({ height = 300, children, ...props }) {
  const Wrapper = styled.pre`
    height: ${height}px;
    margin-bottom: ${SPACE_XS};
    border: ${border1};
    background-color: ${grey1};
    border-radius: 0;
  `;

  return (
    <Wrapper style={{ height: height }} {...props}>
      <Text>{children}</Text>
    </Wrapper>
  );
}

JsonObjectOutputWrapper.propTypes = {
  /*Optional height*/
  height: PropTypes.number,
  /*the content to display in the div*/
  children: PropTypes.oneOfType([PropTypes.object, PropTypes.string]),
  /*any extra attributes to add to JsonOutputWrapper*/
  props: PropTypes.object
};
