import React from "react";
import { PlusCircleTwoTone } from "@ant-design/icons";
import { Button } from "antd";

export function AddNewButton({ text, href, onClick, ...props }) {
  return (
    <Button type={"primary"} href={href} onClick={onClick} {...props}>
      <PlusCircleTwoTone />
      {text}
    </Button>
  );
}
