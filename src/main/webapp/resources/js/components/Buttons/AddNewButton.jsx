import React from "react";
import { Button } from "antd";
import { IconPlusCircle } from "../icons/Icons";
import { primaryColour } from "../../utilities/theme-utilities";

/**
 * React component to be used any time a "create new item" button needs
 * to be added to the page.
 * @param {string} text to be displayed on the button
 * @param {string} href (optional) if this is a link to a new page
 * @param {function} onClick click handler for action to be taken on click
 * @param {object} props any other props that should be added to the button
 * @returns {*}
 */
export function AddNewButton({
  type = "primary",
  text,
  href,
  onClick,
  ...props
}) {
  return (
    <Button
      type={type}
      icon={<IconPlusCircle twoToneColor={primaryColour} />}
      href={href}
      onClick={onClick}
      {...props}
    >
      {text}
    </Button>
  );
}
