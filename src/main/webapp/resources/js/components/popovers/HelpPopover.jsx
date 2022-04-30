import React from "react";
import { Popover } from "antd";
import { IconQuestionCircle } from "../icons/Icons";
import { grey6 } from "../../styles/colors";

/**
 * This creates a help (?) icon with a popover that will contain the contents passed
 * in props
 * @param {object} props
 * @returns {*}
 * @constructor
 */
export function HelpPopover({
  placement = "right",
  width = 250,
  title,
  content,
}) {
  return (
    <Popover
      content={<div style={{ width }}>{content}</div>}
      title={title}
      placement={placement}
    >
      <IconQuestionCircle
        style={{
          margin: "0 4px",
          cursor: "help",
          color: grey6,
        }}
      />
    </Popover>
  );
}