import React, { useState } from "react";
import { Form, Radio } from "antd";
import { useLaunchState } from "../pipeline/launch-context";

export function ReferenceFiles() {
  const { files } = useLaunchState();
  const [current, setCurrent] = useState(files[0].id);

  return (
    <Form.Item label={"Reference File"}>
      <Radio.Group onChange={(e) => setCurrent(e.target.value)} value={current}>
        {files.map((file) => (
          <Radio key={file.id} value={file.id}>
            {file.name}
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
}
