import React, { useState } from "react";
import { Radio } from "antd";

export function ReferenceFiles({ files = [] }) {
  const [current, setCurrent] = useState(files[0].id);

  return (
    <Radio.Group onChange={(e) => setCurrent(e.target.value)} value={current}>
      {files.map((file) => (
        <Radio key={file.id} value={file.id}>
          {file.name}
        </Radio>
      ))}
    </Radio.Group>
  );
}
