import React from "react";
import { Button } from "antd";

export interface IconButtonProps {
  children: React.ReactElement;
  onClick: () => void;
}

/**
 * React component to render an icon as a button, this allows for proper web
 * semantics and accessibility.
 *
 * @param {element} children - icon to render
 * @param {function} onClick - handle what happens when the button is clicked.
 * @returns {*}
 * @constructor
 */
export const IconButton = ({
  children,
  onClick,
}: IconButtonProps): JSX.Element => (
  <Button
    type="link"
    style={{
      border: "none",
      margin: 0,
      padding: 0,
      backgroundColor: "transparent",
    }}
    onClick={onClick}
  >
    {children}
  </Button>
);
