import React from "react";
import { Alert, Form, Radio, Space, Tag } from "antd";
import { useLaunch } from "../launch-context";
import { UploadReferenceFile } from "./UploadReferenceFile";
import { SectionHeading } from "../../../components/ant.design/SectionHeading";
import { SPACE_LG } from "../../../styles/spacing";

/**
 * React component for selecting and uploading reference files for a pipeline
 * if required.
 *
 * @returns {JSX.Element|null}
 * @constructor
 */
export function ReferenceFiles({ form }) {
  const [{ requiresReference, referenceFiles }] = useLaunch();

  return requiresReference ? (
    <Space
      className="t-reference-files"
      direction="vertical"
      style={{ width: `100%` }}
    >
      <SectionHeading id="launch-references">
        {i18n("ReferenceFiles.label")}
      </SectionHeading>
      <Form.Item
        label={i18n("ReferenceFiles.label")}
        name="reference"
        rules={[
          {
            required: true,
            message: (
              <span className="t-ref-error">
                {i18n("ReferenceFiles.required")}
              </span>
            ),
          },
        ]}
      >
        {referenceFiles.length ? (
          <Radio.Group style={{ width: "100%" }} name="reference">
            {referenceFiles.map((file) => (
              <Radio key={`ref-${file.id}`} value={file.id}>
                {file.name}
                {file.projectName ? (
                  <Tag style={{ marginLeft: SPACE_LG }}>{file.projectName}</Tag>
                ) : null}
              </Radio>
            ))}
          </Radio.Group>
        ) : (
          <Alert
            className="t-ref-alert"
            type="info"
            showIcon
            message={i18n("ReferenceFiles.not-found.title")}
            description={i18n("ReferenceFiles.not-found.subTitle")}
          />
        )}
      </Form.Item>
      <UploadReferenceFile form={form} />
    </Space>
  ) : null;
}
