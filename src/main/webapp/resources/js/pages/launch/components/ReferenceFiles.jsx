import React from "react";
import { Divider, Form, Radio, Result, Tag } from "antd";
import { useLaunchDispatch, useLaunchState } from "../launch-context";
import { UploadReferenceFile } from "./UploadReferenceFile";
import styled from "styled-components";
import { grey2 } from "../../../styles/colors";
import { SPACE_XS } from "../../../styles/spacing";

const RadioItem = styled.button`
  padding: ${SPACE_XS};
  transition: all ease-in 0.3s;
  border: 1px dashed transparent;
  display: flex;
  justify-content: space-between;
  width: 100%;
  background-color: transparent;
  &:hover {
    background-color: ${grey2};
    border: 1px dashed rgb(217, 217, 217);
    cursor: pointer;
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

  const setReferenceFile = (e, file) => {
    e.preventDefault();
    dispatchUseReferenceFileById(file.id);
  };

  return requiresReference ? (
    <section>
      {referenceFiles.length ? (
        <Form.Item label={i18n("ReferenceFiles.label")}>
          <Radio.Group style={{ width: "100%" }} value={referenceFile}>
            {referenceFiles.map((file) => (
              <RadioItem
                key={`file-${file.id}`}
                onClick={(e) => setReferenceFile(e, file)}
              >
                <Radio value={file.id}>{file.name}</Radio>
                {file.projectName ? <Tag>{file.projectName}</Tag> : null}
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
