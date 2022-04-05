import React, { useEffect, useState } from "react";
import { Tooltip } from "antd";
import { IconEdit } from "../icons/Icons";
import { IconButton } from "../Buttons";
import { blue6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";

/**
 * React component to be used when the text field for the Ant Design Paragraph
 * editable is not enough, example you need a select input.
 *
 * @param {string} value - current value of the input
 * @param {string} valueClassName = class name for the field
 * @param {element} children - the input to render when editing
 * @returns {*}
 * @constructor
 */
export function EditableParagraph({ value, valueClassName, children }) {
  const [editing, setEditing] = useState(false);

  useEffect(() => {
    setEditing(false);
  }, [value]);

  return editing ? (
    children
  ) : (
    <div className={valueClassName}>
      <span style={{ marginRight: SPACE_XS }}>{value}</span>
      <Tooltip title={i18n("EditableParagraph.tooltip")}>
        <span>
          <IconButton onClick={() => setEditing(true)}>
            <IconEdit style={{ color: blue6 }} />
          </IconButton>
        </span>
      </Tooltip>
    </div>
  );
}
