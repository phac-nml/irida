import React from "react";
import { Icon, Popover } from "antd";
import PropTypes from "prop-types";

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
      <Icon
        type="question-circle-o"
        style={{
          color: "RGBA(46, 149, 248, 1.00)",
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
