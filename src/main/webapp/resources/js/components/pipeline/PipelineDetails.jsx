import React, { useEffect, useRef, useState } from "react";
import { Form, Input } from "antd";

export function PipelineDetails({}) {
  const [name, setName] = useState("");

  const selectNameText = () => nameRef.current.select();
  const formatName = (n) => `${n.replace(/ /g, "_")}_${Date.now()}`;

  useEffect(() => {
    setTimeout(() => nameRef.current.focus(), 100);
  }, []);

  const nameRef = useRef();
  return (
    <Form layout="vertical">
      <Form.Item label={"NAME"}>
        <Input
          type={"text"}
          value={name}
          ref={nameRef}
          onFocus={selectNameText}
          onChange={updateName}
        />
      </Form.Item>
      <Form.Item label={"DESCRIPTION"}>
        <Input.TextArea />
      </Form.Item>
    </Form>
  );
}
