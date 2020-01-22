import React from "react";
import { Popover } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faQuestionCircle } from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";

/**
 * This creates a help (?) icon with a popover that will contain the contents passed
 * in props
 * @param {object} props
 * @returns {*}
 * @constructor
 */
export function HelpPopover(props) {
  return (
    <Popover content={props.content} title={props.title}>
      <div style={{ display: "inline-block" }}>
        <FontAwesomeIcon
          icon={faQuestionCircle}
          style={{
            color: "RGBA(46, 149, 248, 1.00)",
            margin: "0 .5rem",
            cursor: "help"
          }}
        />
      </div>
    </Popover>
  );
}

HelpPopover.propTypes = {
  title: PropTypes.string,
  content: PropTypes.object.isRequired
};
