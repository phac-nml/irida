import React from "react";
import { Divider, Form, Radio, Result } from "antd";
import { useLaunchState } from "../launch-context";
import { UploadReferenceFile } from "./UploadReferenceFile";

/**
 * React component for selecting and uploading reference files for a pipeline
 * if required.
 *
 * @returns {JSX.Element|null}
 * @constructor
 */
export function ReferenceFiles() {
  const { requiresReference, referenceFiles } = useLaunchState();

  return requiresReference ? (
    <section>
      {referenceFiles.length ? (
        <Form.Item label={i18n("ReferenceFiles.label")} name="referenceFile">
          <Radio.Group>
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
          title={i18n("ReferenceFiles.not-found.title")}
          subTitle={i18n("ReferenceFiles.not-found.subTitle")}
        />
      )}
      <UploadReferenceFile />
      <Divider />
    </section>
  ) : null;
}
