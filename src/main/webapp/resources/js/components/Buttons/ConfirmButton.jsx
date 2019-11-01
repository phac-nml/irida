import React from "react";
import { Button, Popconfirm } from "antd";

export function ConfirmButton({title, onConfirm, label, ...props}) {
  return (
    <Popconfirm
      title={title}
      onConfirm={onConfirm}
      {...props}
    >
      <Button type="link">{label}</Button>
    </Popconfirm>
  );
}
