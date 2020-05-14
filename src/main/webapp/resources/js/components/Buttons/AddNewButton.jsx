import React from "react";
import { Button } from "antd";
import { IconPlusCircle } from "../icons/Icons";

/**
 * React component to be used any time a "create new item" button needs
 * to be added to the page.
 * @param {string} text to be displayed on the button
 * @param {string} href (optional) if this is a link to a new page
 * @param {function} onClick click handler for action to be taken on click
 * @param {object} props any other props that should be added to the button
 * @returns {*}
 */
export function AddNewButton({ text, href, onClick, ...props }) {
  return (
    <Button type={"primary"} href={href} onClick={onClick} {...props}>
      <IconPlusCircle />
      {text}
    </Button>
  );
}
