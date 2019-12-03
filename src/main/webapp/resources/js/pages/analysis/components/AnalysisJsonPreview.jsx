/**
 * @File component renders a JSON preview of output files.
 */

import React, { useEffect, useState } from "react";
import { Divider, Row } from "antd";
import { getDataViaChunks } from "../../../apis/analysis/analysis";
import { BasicList } from "../../../components/lists/BasicList";
import { SPACE_XS } from "../../../styles/spacing";
import { isAdmin } from "../../../contexts/AnalysisContext";
import { OutputFileHeader } from "../../../components/OutputFiles/OutputFileHeader";
import { JsonOutputWrapper } from "../../../components/OutputFiles/JsonOutputWrapper";

export default function AnalysisJsonPreview({ output }) {
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
        if (parsedJson[val] !== undefined) {
          Object.entries(parsedJson[val]).map(fileRowData => {
            jsonListData.push({
              title: fileRowData[0],
              desc: fileRowData[1] !== null ? fileRowData[1].toString() : ""
            });
          });
          setJsonData(jsonListData);
        }
      });
    });
  }, []);

  /*
   * Sets numRows to current numRows + 10 on scroll.
   * Used to load n + 10 amount of rows from
   * data at a time.
   */

  function showMoreRows() {
    let element = document.getElementById(
      `json-${output.filename.replace(".", "-")}`
    );

    if (element.scrollTop + 300 >= element.scrollHeight) {
      setNumRows(numRows + 10);
    }
  }

  return jsonData !== null ? (
    <div>
      <Row>
        <OutputFileHeader output={output} />
      </Row>
      {isAdmin ? (
        <Row>
          <JsonOutputWrapper
            id={`json-${output.filename.replace(".", "-")}`}
            style={{ padding: SPACE_XS }}
            onScroll={() => showMoreRows()}
          >
            <BasicList dataSource={jsonData.slice(0, numRows)} loading={true} />
          </JsonOutputWrapper>
          <Divider />
        </Row>
      ) : null}
    </div>
  ) : null;
}
