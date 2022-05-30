import React from "react";
import { Avatar, Button, List, Space, Tag } from "antd";
import { SequenceFileHeader } from "./SequenceFileHeader";
import { IconDownloadFile, IconFile } from "../icons/Icons";
import { useDispatch, useSelector } from "react-redux";

import { FastQC } from "../samples/components/fastqc/FastQC";
import { setFastQCModalData } from "../samples/components/fastqc/fastQCSlice";

/**
 * React component to display single end file details
 *
 * @param {array} files
 * @param fastqcResults
 * @param {function} download assembly file function
 * @param {function} download sequence file function
 * @param {function} remove files from sample function
 * @param {function} get file processing state function
 * @param qcEntryTranslationKeys
 * @param displayConcatenationCheckbox Whether to display checkbox or not
 * @param {function} set default sequencing object for sample
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
  displayConcatenationCheckbox = false,
  updateDefaultSequencingObject = null,
  autoDefaultPair = null,
}) {
  const { sample } = useSelector((state) => state.sampleReducer);
  const { fastQCModalVisible, sequencingObjectId } = useSelector(
    (state) => state.fastQCReducer
  );

  const dispatch = useDispatch();
  /*
  Function to download the sequence file or genome assembly
  depending on the file type
   */
  const downloadFile = (file) => {
    if (file.fileType === "assembly") {
      downloadAssemblyFile({
        sampleId: sample.identifier,
        genomeAssemblyId: file.fileInfo.identifier,
      });
    } else {
      downloadSequenceFile({
        sequencingObjectId: file.fileInfo.identifier,
        sequenceFileId: file.fileInfo.sequenceFile
          ? file.fileInfo.sequenceFile.identifier
          : file.fileInfo.file.identifier,
      });
    }
  };

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
            displayConcatenationCheckbox={displayConcatenationCheckbox}
            updateDefaultSequencingObject={updateDefaultSequencingObject}
            autoDefaultFirstPair={autoDefaultPair}
          />
        </List.Item>,
        <List.Item
          key={`file-${file.id}`}
          style={{ width: `100%` }}
          className="t-file-details"
        >
          <List.Item.Meta
            avatar={
              <Avatar
                size={`small`}
                style={file.fileType !== "assembly" && { marginTop: 3 }}
                icon={<IconFile />}
              />
            }
            title={
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                {fastqcResults &&
                file.fileInfo.processingState === "FINISHED" ? (
                  <div>
                    <Button
                      type="link"
                      style={{ padding: 0 }}
                      onClick={() =>
                        dispatch(
                          setFastQCModalData({
                            fileLabel: file.fileInfo.label,
                            fileId: file.fileInfo.sequenceFile
                              ? file.fileInfo.sequenceFile.identifier
                              : file.fileInfo.file.identifier,
                            sequencingObjectId: file.fileInfo.identifier,
                            fastQCModalVisible: true,
                            processingState: file.fileInfo.processingState,
                          })
                        )
                      }
                    >
                      <span className="t-file-label">
                        {file.fileInfo.label}
                      </span>
                    </Button>
                    {fastQCModalVisible &&
                    sequencingObjectId === file.fileInfo.identifier ? (
                      <FastQC />
                    ) : null}
                  </div>
                ) : (
                  <span className="t-file-label">{file.fileInfo.label}</span>
                )}

                <Space direction="horizontal" size="small">
                  {file.fileType === "assembly"
                    ? null
                    : getProcessingState(file.fileInfo.processingState)}
                  <span className="t-file-size">{file.firstFileSize}</span>
                  <Button
                    shape="circle"
                    icon={<IconDownloadFile />}
                    className="t-download-file-btn"
                    onClick={() => downloadFile(file)}
                  />
                </Space>
              </div>
            }
          />
        </List.Item>,
        file.fileType === "sequencingObject" && file.qcEntries?.length ? (
          <List.Item key={`file-${file.id}-qc-entry`} style={{ width: `100%` }}>
            <List.Item.Meta
              title={file.qcEntries.map((entry) => {
                return (
                  <Tag
                    key={`file-${file.id}-qc-entry-status`}
                    color={entry.status === "POSITIVE" ? "green" : "red"}
                  >
                    {qcEntryTranslations[entry.type]}{" "}
                    {entry.message ? entry.message : ""}
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
