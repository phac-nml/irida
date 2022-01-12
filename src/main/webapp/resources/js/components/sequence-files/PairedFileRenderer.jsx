import React from "react";
import { List } from "antd";
import { ArrowLeftOutlined, ArrowRightOutlined } from "@ant-design/icons";
import { SequenceFileDetailsRenderer } from "./SequenceFileDetailsRenderer";
import { SequenceFileHeader } from "./SequenceFileHeader";
import { setBaseUrl } from "../../utilities/url-utilities";
import { useSelector } from "react-redux";
/**
 * React component to display paired end file details
 *
 * @param {array} pair
 * @function download sequence file function
 * @function remove files from sample function
 * @function get file processing state function
 * @returns {JSX.Element}
 * @constructor
 */
export function PairedFileRenderer({
  pair,
  downloadSequenceFile = () => {},
  removeSampleFiles = () => {},
  getProcessingState = () => {},
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
        />
      }
      layout={`vertical`}
      dataSource={files}
      renderItem={(file) => {
        return (
          <SequenceFileDetailsRenderer
            file={file}
            isForwardFile={file.forwardFile}
            fileObjectId={pair.fileInfo.identifier}
            downloadSequenceFile={downloadSequenceFile}
            getProcessingState={getProcessingState}
          />
        );
      }}
    />
  );
}
