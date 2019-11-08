/**
 * @file ConfirmButton is a button wrapped in a Popconfirm.  Use when
 *       the user should be asked for confirmation of an action.
 */
import React from "react";
import { Button, Popconfirm } from "antd";

/**
 * Renders a Button component wrapped in a Popconfirm
 * @param {string} title - Title on the Popconfirm
 * @param {function} onConfirm - What to do when the ok is clicked on the Popconfirm
 * @param {string} label - Text for the button
 * @param {object} props - any remaining properties to pass in
 * @returns {*}
 * @constructor
 */
export function ConfirmButton({title, onConfirm, label, ...props}) {
  return (
    <Popconfirm
      title={title}
      onConfirm={onConfirm}
    >
      <Button type="link" {...props}>{label}</Button>
    </Popconfirm>
  );
}
