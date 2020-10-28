import React from "react";
import { Button, Form, Input } from "antd";
import { useLaunchState } from "../../launch-context";

export function SaveParametersAsFooter({ onCancel }) {
  const { parameterSet } = useLaunchState();

  return (
    <Form layout="vertical">
      <Form.Item label={i18n("SaveParametersAsFooter.name")}>
        <Input defaultValue={`${parameterSet.label} (copy)`} />
      </Form.Item>
      <Button onClick={onCancel}>
        {i18n("SaveParametersAsFooter.cancel")}
      </Button>
      <Button onClick={() => {}}>{i18n("SaveParametersAsFooter.save")}</Button>
    </Form>
  );
}
