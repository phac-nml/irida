import React, { useState } from "react";
import { Form, Radio, Result } from "antd";
import { useLaunchState } from "../pipeline-launch/launch-context";
import { UploadReferenceFile } from "./UploadReferenceFile";

export function ReferenceFiles() {
  const { files } = useLaunchState();
  const [current, setCurrent] = useState(
    files && files.length ? files[0].id : undefined
  );

  return (
    <section>
      {current ? (
        <Form.Item label={"Reference File"}>
          <Radio.Group
            onChange={(e) => setCurrent(e.target.value)}
            value={current}
          >
            {files.map((file) => (
              <Radio key={file.id} value={file.id}>
                {file.name}
              </Radio>
            ))}
          </Radio.Group>
        </Form.Item>
      ) : (
        <Result
          status="404"
          title={"Cannot find any reference files for your projects :("}
          subTitle={"You can upload a custom reference file below."}
        />
      )}
      <UploadReferenceFile />
    </section>
  );
}
