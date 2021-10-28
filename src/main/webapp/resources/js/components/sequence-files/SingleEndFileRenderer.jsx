import React from "react";
import { Avatar, Button, List } from "antd";
import { SequenceFileHeader } from "./SequenceFileHeader";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconDownloadFile, IconFile, IconRemove } from "../icons/Icons";
import { SPACE_XS } from "../../styles/spacing";

/**
 * React component to display single end file details
 *
 * @param {array} files
 * @param sampleId
 * @param fastqcResults
 * @returns {JSX.Element}
 * @constructor
 */
export function SingleEndFileRenderer({
  files,
  sampleId,
  fastqcResults = true,
}) {
  return (
    <List
      bordered
      dataSource={files}
      renderItem={(file) => [
        <List.Item>
          <SequenceFileHeader file={file.fileInfo} />
        </List.Item>,
        <List.Item key={`file-${file.id}`} style={{ width: `100%` }}>
          <List.Item.Meta
            avatar={<Avatar size={`small`} icon={<IconFile />} />}
            title={
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                {fastqcResults ? (
                  <a
                    href={
                      file.fileInfo.sequenceFile
                        ? setBaseUrl(
                            `samples/${sampleId}/sequenceFiles/${file.fileInfo.identifier}/file/${file.fileInfo.sequenceFile.identifier}`
                          )
                        : setBaseUrl(
                            `samples/${sampleId}/sequenceFiles/${file.fileInfo.identifier}/file/${file.fileInfo.file.identifier}`
                          )
                    }
                    target="_blank"
                  >
                    {file.fileInfo.label}
                  </a>
                ) : (
                  <span>{file.fileInfo.label}</span>
                )}

                <span>
                  <span style={{ marginRight: SPACE_XS }}>
                    {file.firstFileSize}
                  </span>
                  <Button
                    style={{ marginRight: SPACE_XS }}
                    shape="circle"
                    icon={<IconDownloadFile />}
                  />
                  <Button shape="circle" icon={<IconRemove />} />
                </span>
              </div>
            }
          />
        </List.Item>,
      ]}
    />
  );
}
