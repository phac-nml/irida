import React from "react";
import { Popover } from "antd";

import PropTypes from "prop-types";
import { IconQuestionCircle } from "../icons/Icons";
import { grey6 } from "../../styles/colors";

/**
 * This create a help (?) icon with a popover that will contain the contents passed
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
          margin: "0 .5rem",
          cursor: "help",
          color: grey6,
        }}
      />
    </Popover>
  );
}

HelpPopover.propTypes = {
  title: PropTypes.string,
  content: PropTypes.object.isRequired,
};
