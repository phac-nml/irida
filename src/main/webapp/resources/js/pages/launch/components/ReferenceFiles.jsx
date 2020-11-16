import React from "react";
import { Divider, Form, Radio, Result } from "antd";
import { useLaunchDispatch, useLaunchState } from "../launch-context";
import { UploadReferenceFile } from "./UploadReferenceFile";
import styled from "styled-components";
import { SPACE_XS } from "../../../styles/spacing";
import { grey2 } from "../../../styles/colors";

const RadioItem = styled(Radio)`
  padding: ${SPACE_XS};
  border-radius: 2px;
  transition: background-color ease-in 0.3s;
  &:hover {
    background-color: ${grey2};
  }
`;

/**
 * React component for selecting and uploading reference files for a pipeline
 * if required.
 *
 * @returns {JSX.Element|null}
 * @constructor
 */
export function ReferenceFiles() {
  const { requiresReference, referenceFiles, referenceFile } = useLaunchState();
  const { dispatchUseReferenceFileById } = useLaunchDispatch();

  return requiresReference ? (
    <section>
      {referenceFiles.length ? (
        <Form.Item label={i18n("ReferenceFiles.label")}>
          <Radio.Group style={{ width: "100%" }} value={referenceFile}>
            {referenceFiles.map((file) => (
              <RadioItem
                key={`file-${file.id}`}
                value={file.id}
                onClick={() => dispatchUseReferenceFileById(file.id)}
              >
                {file.name}
              </RadioItem>
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
