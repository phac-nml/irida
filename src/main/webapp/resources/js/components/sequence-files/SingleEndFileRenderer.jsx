import React from "react";
import { Avatar, Button, List, Space, Tag } from "antd";
import { SequenceFileHeader } from "./SequenceFileHeader";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconDownloadFile, IconFile } from "../icons/Icons";
import { SPACE_XS } from "../../styles/spacing";
import { useSelector } from "react-redux";

/**
 * React component to display single end file details
 *
 * @param {array} files
 * @param fastqcResults
 * @function download assembly file function
 * @function download sequence file function
 * @function remove files from sample function
 * @function get file processing state function
 * @param qcEntryTranslationKeys
 * @returns {JSX.Element}
 * @constructor
 */
export function SingleEndFileRenderer({
  files,
  fastqcResults = true,
  downloadAssemblyFile = () => {},
  downloadSequenceFile = () => {},
  removeSampleFiles = () => {},
  getProcessingState = () => {},
  qcEntryTranslations,
}) {
  const { sample } = useSelector((state) => state.sampleReducer);

  return (
    <List
      bordered
      dataSource={files}
      renderItem={(file) => [
        <List.Item key={`file-header-${file.id}`}>
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
                            `samples/${sample.identifier}/sequenceFiles/${file.fileInfo.identifier}/file/${file.fileInfo.sequenceFile.identifier}`
                          )
                        : setBaseUrl(
                            `samples/${sample.identifier}/sequenceFiles/${file.fileInfo.identifier}/file/${file.fileInfo.file.identifier}`
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
                  {file.fileType === "assembly"
                    ? null
                    : getProcessingState(file.fileInfo.processingState)}
                  <span style={{ marginRight: SPACE_XS }}>
                    {file.firstFileSize}
                  </span>
                  <Button
                    shape="circle"
                    icon={<IconDownloadFile />}
                    onClick={() =>
                      file.fileType === "assembly"
                        ? downloadAssemblyFile({
                            sampleId: sample.identifier,
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
        file.fileType === "sequencingObject" && file.qcEntries !== null ? (
          <List.Item key={`file-${file.id}-qc-entry`} style={{ width: `100%` }}>
            <List.Item.Meta
              title={file.qcEntries.map((entry) => {
                return (
                  <Tag
                    key={`file-${file.id}-qc-entry-status`}
                    color={entry.status === "POSITIVE" ? "green" : "red"}
                  >
                    {qcEntryTranslations[entry.type] + entry.message}
                  </Tag>
                );
              })}
            />
          </List.Item>
        ) : null,
      ]}
    />
  );
}
