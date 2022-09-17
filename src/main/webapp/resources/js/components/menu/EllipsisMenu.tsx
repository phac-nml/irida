import React from "react";
import { Dropdown, Menu } from "antd";
import { MoreOutlined } from "@ant-design/icons";

export interface EllipsisMenuProps {
  menu: React.ReactElement;
  className?: string;
  placement?:
    | "bottomRight"
    | "topLeft"
    | "topCenter"
    | "topRight"
    | "bottomLeft"
    | "bottomCenter"
    | "top"
    | "bottom"
    | undefined;
  trigger?: ("click" | "hover" | "contextMenu")[];
}

/**
 * React component to display an ellipsis menu
 * @param menu The dropdown menu
 * @param className The class name
 * @param placement Where to position dropdown
 * @param trigger How the dropdown is activated
 * @returns {JSX.Element}
 * @constructor
 */
export function EllipsisMenu({
  menu,
  className = "t-actions-menu",
  placement = "bottomRight",
  trigger = ["click"],
}: EllipsisMenuProps): JSX.Element {
  return (
    <Dropdown
      overlay={menu}
      className={className}
      trigger={trigger}
      placement={placement}
    >
      <MoreOutlined />
    </Dropdown>
  );
}
