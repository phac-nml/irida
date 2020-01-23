import React from "react";
import { Popover } from "antd";
import { QuestionCircleTwoTone } from "@ant-design/icons";
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
        <QuestionCircleTwoTone
          style={{
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
