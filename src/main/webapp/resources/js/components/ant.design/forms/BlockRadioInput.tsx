import React from "react";
import styled from "styled-components";
import { SPACE_XS } from "../../../styles/spacing";
import { grey2, grey4 } from "../../../styles/colors";

const RadioItem = styled.button`
  padding: ${SPACE_XS};
  transition: all ease-in 0.3s;
  border: 1px dashed transparent;
  display: flex;
  justify-content: space-between;
  width: 100%;
  background-color: transparent;

  &:hover {
    background-color: ${grey2};
    border: 1px dashed ${grey4};
    cursor: pointer;
  }
`;

interface BlockRadioInputProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children?: React.ReactNode;
}

/**
 * React component to Render a Ant Design Radio button in block.
 *
 * @param children
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
export function BlockRadioInput({
  children,
  ...props
}: BlockRadioInputProps): JSX.Element {
  return <RadioItem {...props}>{children}</RadioItem>;
}
