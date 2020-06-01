import React from "react";
import { Popover } from "antd";

import PropTypes from "prop-types";
import { IconQuestionCircle } from "../icons/Icons";

/**
 * This create a help (?) icon with a popover that will contain the contents passed
 * in props
 * @param {object} props
 * @returns {*}
 * @constructor
 */
export function HelpPopover(props) {
  return (
    <Popover content={props.content} title={props.title}>
      <IconQuestionCircle
        style={{
          margin: "0 .5rem",
          cursor: "help"
        }}
      />
    </Popover>
  );
}

HelpPopover.propTypes = {
  title: PropTypes.string,
  content: PropTypes.object.isRequired
};
