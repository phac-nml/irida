/**
 * @File component renders a text preview of output files.
 */

import React, { useEffect, useState } from "react";
import { Divider, Space, Typography } from "antd";
import { getDataViaChunks } from "../../../apis/analysis/analysis";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import {
  fileSizeLoaded,
  getNewChunkSize,
} from "../../../utilities/file-utilities";
import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";
import { OutputFileHeader } from "../../../components/OutputFiles";

const { Text } = Typography;
const scrollableDivHeight = 300;

const TextOutputWrapper = styled.pre`
  height: ${scrollableDivHeight}px;
  width: 100%;
  overflow: auto;
  margin-bottom: ${SPACE_XS};
  white-space: pre-wrap;
  background-color: #ffffff;
`;

export default function AnalysisTextPreview({ output }) {
  const [fileRows, setFileRows] = useState([]);
  const [filePointer, setFilePointer] = useState(0);
  const [savedText, setSavedText] = useState("");
  const chunkSize = 8192;
  const [loading, setLoading] = useState(false);
  const [fileSizePreview,setFileSizePreview] = useState("");

  /*
   * Get n bytes of text file output data on load and set
   * the fileRows local state to this data.
   */
  useEffect(() => {
    setLoading(true);
    getDataViaChunks({
      submissionId: output.analysisSubmissionId,
      fileId: output.id,
      seek: 0,
      chunk: getNewChunkSize(0, output.fileSizeBytes, chunkSize),
    }).then((data) => {
      setSavedText(data.text);
      setFilePointer(data.filePointer);
      setFileRows(data.text);
      setFileSizePreview(fileSizeLoaded(data.filePointer, output.fileSizeBytes))
      setLoading(false);
    });
  }, []);

  /*
   * Get n bytes of text file output data on scroll and set
   * the fileRows local state to this data added to savedText.
   */
  function loadMoreData() {
    const scollElement = document.getElementById(
      `text-${output.filename.replace(".", "-")}`
    );

    if (
      scollElement.scrollTop + scrollableDivHeight >=
        scollElement.scrollHeight - 1 &&
      getNewChunkSize(filePointer, output.fileSizeBytes, chunkSize) >= 0 &&
      filePointer < output.fileSizeBytes - 1
    ) {
      setLoading(true);
      getDataViaChunks({
        submissionId: output.analysisSubmissionId,
        fileId: output.id,
        seek: filePointer,
        chunk: getNewChunkSize(filePointer, output.fileSizeBytes, chunkSize),
      }).then((data) => {
        if (data.text !== null) {
          setSavedText(savedText + data.text);
          setFilePointer(data.filePointer);
          setFileRows(savedText + data.text);
          setFileSizePreview(fileSizeLoaded(data.filePointer, output.fileSizeBytes));
        }
        setLoading(false);
      });
    }
  }

  /*
   * Displays the scrollable div with text output as well
   * as the name of the file and a download button for
   * the file
   */
  function displayTextOutput() {
    if (fileRows.length > 0) {
      return (
        <div>
          <OutputFileHeader output={output} />
          <TextOutputWrapper
            id={`text-${output.filename.replace(".", "-")}`}
            onScroll={() => loadMoreData()}
          >
            <Text>{fileRows}</Text>
          </TextOutputWrapper>
          <div>
            <Space direction={"horizontal"}>
              <span id={`${output.filename}-preview-status`}>{fileSizePreview}</span>
              <span id={`${output.filename}-loading`}>
                {loading ? (
                  <ContentLoading
                    message={i18n("AnalysisOutputs.retrievingFileData")}
                  />
                ) : null}
              </span>
            </Space>
          </div>
          <Divider />
        </div>
      );
    }
  }

  return <>{fileRows !== null ? displayTextOutput() : <ContentLoading />}</>;
}
