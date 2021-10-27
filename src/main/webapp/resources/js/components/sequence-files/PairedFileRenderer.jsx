import React from "react";
import { List } from "antd";
import { ArrowLeftOutlined, ArrowRightOutlined } from "@ant-design/icons";
import { SequenceFileDetailsRenderer } from "./SequenceFileDetailsRenderer";
import { SequenceFileHeader } from "./SequenceFileHeader";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * React component to display paired end file details
 *
 * @param {array} pair
 * @param sampleId
 * @returns {JSX.Element}
 * @constructor
 */
export function PairedFileRenderer({ pair, sampleId }) {
  console.log(pair);
  const files = [
    {
      label: pair.fileInfo.forwardSequenceFile.label,
      id: pair.fileInfo.forwardSequenceFile.identifier,
      icon: <ArrowRightOutlined />,
      filesize: pair.firstFileSize,
      fastqcLink: setBaseUrl(
        `samples/${sampleId}/sequenceFiles/${pair.fileInfo.identifier}/file/${pair.fileInfo.forwardSequenceFile.identifier}`
      ),
    },
    {
      label: pair.fileInfo.reverseSequenceFile.label,
      id: pair.fileInfo.reverseSequenceFile.identifier,
      icon: <ArrowLeftOutlined />,
      filesize: pair.secondFileSize,
      fastqcLink: setBaseUrl(
        `samples/${sampleId}/sequenceFiles/${pair.fileInfo.identifier}/file/${pair.fileInfo.reverseSequenceFile.identifier}`
      ),
    },
  ];

  return (
    <List
      bordered
      header={<SequenceFileHeader file={pair.fileInfo} />}
      layout={`vertical`}
      dataSource={files}
      renderItem={(file) => <SequenceFileDetailsRenderer file={file} />}
    />
  );
}
