import React from "react";
import { Avatar, Button, List, Space } from "antd";
import { SequenceFileHeader } from "./SequenceFileHeader";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconDownloadFile, IconFile } from "../icons/Icons";
import { SPACE_XS } from "../../styles/spacing";

/**
 * React component to display single end file details
 *
 * @param {array} files
 * @param sampleId
 * @param fastqcResults
 * @function download assembly file function
 * @function download sequence file function
 * @function remove files from sample function
 * @returns {JSX.Element}
 * @constructor
 */
export function SingleEndFileRenderer({
  files,
  sampleId,
  fastqcResults = true,
  downloadAssemblyFile = () => {},
  downloadSequenceFile = () => {},
  removeSampleFiles,
}) {
  return (
    <List
      bordered
      dataSource={files}
      renderItem={(file) => [
        <List.Item>
          <SequenceFileHeader
            file={file.fileInfo}
            removeSampleFiles={removeSampleFiles}
            fileObjectId={file.fileInfo.identifier}
            type={file.fileType}
          />
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

                <Space direction="horizontal" size="small">
                  <span style={{ marginRight: SPACE_XS }}>
                    {file.firstFileSize}
                  </span>
                  <Button
                    shape="circle"
                    icon={<IconDownloadFile />}
                    onClick={() =>
                      file.fileType === "assembly"
                        ? downloadAssemblyFile({
                            sampleId,
                            genomeAssemblyId: file.fileInfo.identifier,
                          })
                        : downloadSequenceFile({
                            sequencingObjectId: file.fileInfo.identifier,
                            sequenceFileId: file.fileInfo.sequenceFile
                              ? file.fileInfo.sequenceFile.identifier
                              : file.fileInfo.file.identifier,
                          })
                    }
                  />
                </Space>
              </div>
            }
          />
        </List.Item>,
      ]}
    />
  );
}
