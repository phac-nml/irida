import React from "react";
import { Button } from "antd";
import { useSelector } from "react-redux";

export function MetadataAddTemplateField() {
  const { fields } = useSelector((state) => state.fields);
  const [visible, setVisi];
  return (
    <>
      <Button></Button>
    </>
  );
}
