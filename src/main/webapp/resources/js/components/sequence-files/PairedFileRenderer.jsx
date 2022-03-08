import React from "react";
import { List, Tag } from "antd";
import { ArrowLeftOutlined, ArrowRightOutlined } from "@ant-design/icons";
import { SequenceFileDetailsRenderer } from "./SequenceFileDetailsRenderer";
import { SequenceFileHeader } from "./SequenceFileHeader";
import { setBaseUrl } from "../../utilities/url-utilities";
import { useSelector } from "react-redux";

/**
 * React component to display paired end file details
 *
 * @param {array} pair
 * @param {function} download sequence file function
 * @param {function} remove files from sample function
 * @param {function} get file processing state function
 * @param qcEntryTranslationKeys Translation keys for qc entries
 * @param displayConcatenationCheckbox Whether to display checkbox or not
 * @param {function} set default sequencing object for sample
 * @param autoDefaultFirstPair the first pair in the list of pairs
 * @returns {JSX.Element}
 * @constructor
 */
export function PairedFileRenderer({
  pair,
  downloadSequenceFile = () => {},
  removeSampleFiles = () => {},
  getProcessingState = () => {},
  qcEntryTranslations,
  displayConcatenationCheckbox = false,
  updateDefaultSequencingObject = null,
  autoDefaultFirstPair,
}) {
  const { sample } = useSelector((state) => state.sampleReducer);

  const files = [
    {
      label: pair.fileInfo.forwardSequenceFile.label,
      id: pair.fileInfo.forwardSequenceFile.identifier,
      icon: <ArrowRightOutlined />,
      filesize: pair.firstFileSize,
      fastqcLink: setBaseUrl(
        `samples/${sample.identifier}/sequenceFiles/${pair.fileInfo.identifier}/file/${pair.fileInfo.forwardSequenceFile.identifier}`
      ),
      forwardFile: true,
      fileType: pair.fileType,
      processingState: pair.fileInfo.processingState,
    },
    {
      label: pair.fileInfo.reverseSequenceFile.label,
      id: pair.fileInfo.reverseSequenceFile.identifier,
      icon: <ArrowLeftOutlined />,
      filesize: pair.secondFileSize,
      fastqcLink: setBaseUrl(
        `samples/${sample.identifier}/sequenceFiles/${pair.fileInfo.identifier}/file/${pair.fileInfo.reverseSequenceFile.identifier}`
      ),
      forwardFile: false,
      processingState: pair.fileInfo.processingState,
    },
  ];

  return (
    <List
      bordered
      header={
        <SequenceFileHeader
          file={pair.fileInfo}
          fileObjectId={pair.fileInfo.identifier}
          type={pair.fileType}
          removeSampleFiles={removeSampleFiles}
          displayConcatenationCheckbox={displayConcatenationCheckbox}
          updateDefaultSequencingObject={updateDefaultSequencingObject}
          autoDefaultFirstPair={autoDefaultFirstPair}
        />
      }
      layout={`vertical`}
      dataSource={files}
      renderItem={(file) => {
        return (
          <div>
            <SequenceFileDetailsRenderer
              file={file}
              isForwardFile={file.forwardFile}
              fileObjectId={pair.fileInfo.identifier}
              downloadSequenceFile={downloadSequenceFile}
              getProcessingState={getProcessingState}
            />
            {pair.qcEntries?.length && !file.forwardFile ? (
              <List.Item
                key={`file-${file.id}-qc-entry`}
                style={{ width: `100%` }}
              >
                <List.Item.Meta
                  title={pair.qcEntries.map((entry) => {
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
            ) : null}
          </div>
        );
      }}
    />
  );
}
