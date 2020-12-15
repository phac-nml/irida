import React from "react";
import { Alert, Form, Radio, Space, Tag } from "antd";
import { useLaunch } from "../launch-context";
import { UploadReferenceFile } from "./UploadReferenceFile";
import { SectionHeading } from "../SectionHeading";
import { setReferenceFileById } from "../launch-dispatch";
import { BlockRadioInput } from "../../../components/ant.design/forms/BlockRadioInput";

/**
 * React component for selecting and uploading reference files for a pipeline
 * if required.
 *
 * @returns {JSX.Element|null}
 * @constructor
 */
export function ReferenceFiles() {
  const [
    { requiresReference, referenceFiles, referenceFile },
    launchDispatch,
  ] = useLaunch();

  const setReferenceFile = (e, file) => {
    e.preventDefault();
    setReferenceFileById(launchDispatch, file.id);
  };

  return requiresReference ? (
    <Space direction="vertical" style={{ width: `100%` }}>
      <SectionHeading id="launch-references">
        {i18n("ReferenceFiles.label")}
      </SectionHeading>
      {referenceFiles.length ? (
        <Form.Item label={i18n("ReferenceFiles.label")}>
          <Radio.Group style={{ width: "100%" }} value={referenceFile}>
            {referenceFiles.map((file) => (
              <BlockRadioInput
                key={`file-${file.id}`}
                onClick={(e) => setReferenceFile(e, file)}
              >
                <Radio value={file.id}>{file.name}</Radio>
                {file.projectName ? <Tag>{file.projectName}</Tag> : null}
              </BlockRadioInput>
            ))}
          </Radio.Group>
        </Form.Item>
      ) : (
        <Alert
          type="info"
          showIcon
          message={i18n("ReferenceFiles.not-found.title")}
          description={i18n("ReferenceFiles.not-found.subTitle")}
        />
      )}
      <UploadReferenceFile />
    </Space>
  ) : null;
}
