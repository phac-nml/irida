/**
 * @File component renders a tabular preview of output files.
 */

import React, { useEffect, useState } from "react";
import { Divider, Row, Table, Typography } from "antd";
import { getDataViaLines } from "../../../apis/analysis/analysis";
import { parseHeader, parseRows } from "../tabular-preview";
import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";
import { OutputFileHeader } from "../../../components/OutputFiles/OutputFileHeader";
const TabularOutputWrapper = styled.div`
  height: 300px;
  width: 100%;
  margin-bottom: ${SPACE_XS};
`;

export function AnalysisTabularPreview({ output }) {
  const { firstLine, fileExt } = output;
  const fileExtCSV = fileExt === "csv";

  const [fileRows, setFileRows] = useState([]);
  const [fileCols, setFileCols] = useState([]);
  const MAX_TABLE_ROWS_PER_PAGE = 5;

  /*
   * Get tabular file output data from the start of a file to the end
   * and set the fileRows local state to this data.
   * If only n amount of lines are required the start and end variables
   * can be changed as such
   */
  useEffect(() => {
    getDataViaLines({
      start: 0,
      end: null,
      submissionId: output.analysisSubmissionId,
      fileId: output.id
    }).then(data => {
      setFileRows(parseRows(data.lines, data.start, fileExtCSV));
      setFileCols(parseHeader(firstLine, fileExtCSV));
    });
  }, []);

  return (
    <div>
      <Row>
        <OutputFileHeader output={output} />
      </Row>
      <Row>
        <TabularOutputWrapper>
          <Table
            layout="auto"
            columns={fileCols}
            dataSource={fileRows}
            scroll={{ x: true }}
            pagination={{ pageSize: MAX_TABLE_ROWS_PER_PAGE }}
          />
        </TabularOutputWrapper>
        <Divider />
      </Row>
    </div>
  );
}
