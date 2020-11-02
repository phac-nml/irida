import React from "react";
import { Form, Radio, Result } from "antd";
import { useLaunchDispatch, useLaunchState } from "../launch-context";
import { UploadReferenceFile } from "./UploadReferenceFile";

export function ReferenceFiles() {
  const { requiresReference, referenceFiles, referenceFile } = useLaunchState();
  const { dispatchUseReferenceFile } = useLaunchDispatch();

  return requiresReference ? (
    <section>
      {requiresReference ? (
        <Form.Item label={"Reference File"} required>
          <Radio.Group
            onChange={(e) => dispatchUseReferenceFile(e.target.value)}
            value={referenceFile}
          >
            {referenceFiles.map((file) => (
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
  ) : null;
}
