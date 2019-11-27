/**
 * @File component renders a JSON preview of output files.
 */

import React, { useEffect, useState } from "react";
import { Divider, Row, Typography } from "antd";
import { getDataViaChunks } from "../../../apis/analysis/analysis";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { BasicList } from "../../../components/lists/BasicList";
import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";
import { isAdmin } from "../../../contexts/AnalysisContext";
import { OutputFileHeader } from "../../../components/OutputFiles/OutputFileHeader";

const JsonOutputWrapper = styled.div`
  height: 300px;
  width: 100%;
  overflow: auto;
  margin-bottom: ${SPACE_XS};
  border: solid 1px #bdc3c7;
`;

export function AnalysisJsonPreview({ output }) {
  let savedText = "";
  const [jsonData, setJsonData] = useState(null);
  const [numRows, setNumRows] = useState(10);
  /*
   * Get json file output data and set the jsonData local state to this data.
   */
  useEffect(() => {
    getDataViaChunks({
      submissionId: output.analysisSubmissionId,
      fileId: output.id,
      seek: 0,
      chunk: output.fileSizeBytes
    }).then(data => {
      savedText = data.text;
      let parsedJson = JSON.parse(savedText);
      let jsonListData = [];

      Object.keys(parsedJson).map((key, val) => {
        Object.entries(parsedJson[val]).map(fileRowData => {
          jsonListData.push({
            title: fileRowData[0],
            desc: fileRowData[1] !== null ? fileRowData[1].toString() : ""
          });
        });
      });

      setJsonData(jsonListData);
    });
  }, []);

  /*
   * Sets numRows to current numRows + 10 on scroll.
   * Used to load n + 10 amount of rows from
   * data at a time.
   */

  function showMoreRows() {
    let element = document.getElementById(
      `json-${output.filename.split(".")[0]}`
    );

    if (element.scrollTop + 300 >= element.scrollHeight) {
      setNumRows(numRows + 10);
    }
  }

  function displayJson() {
    if (jsonData !== null) {
      return (
        <div>
          <Row>
            <OutputFileHeader output={output} />
          </Row>
          {isAdmin ? (
            <Row>
              <JsonOutputWrapper
                id={`json-${output.filename.split(".")[0]}`}
                style={{ padding: SPACE_XS }}
                onScroll={() => showMoreRows()}
              >
                <BasicList dataSource={jsonData.slice(0, numRows)} />
              </JsonOutputWrapper>
              <div
                style={{ fontWeight: "bold" }}
                id={`${output.filename}-preview-status`}
              ></div>
              <Divider />
            </Row>
          ) : null}
        </div>
      );
    }
  }

  return <>{jsonData !== null ? displayJson() : <ContentLoading />}</>;
}
