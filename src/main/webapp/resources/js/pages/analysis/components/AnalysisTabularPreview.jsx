/**
 * @File component renders a tabular preview of output files.
 */

import React from "react";
import { Divider, Table } from "antd";
import { getDataViaLines } from "../../../apis/analysis/analysis";
import { parseHeader, parseRows } from "../tabular-preview";
import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";
import { OutputFileHeader } from "../../../components/OutputFiles/OutputFileHeader";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";

const TabularOutputWrapper = styled.div`
  width: 100%;
  margin-bottom: ${SPACE_XS};
`;

export function AnalysisTabularPreview({ output }) {
  const { firstLine, fileExt } = output;
  const fileExtCSV = fileExt === "csv";

  const [fileRows, setFileRows] = React.useState([]);
  const [fileCols, setFileCols] = React.useState([]);
  const [loading, setLoading] = React.useState(false);
  const [total, setTotal] = React.useState(0);

  /*
   * Get tabular file output data from the start of a file to the end
   * and set the fileRows local state to this data.
   * If only n amount of lines are required the start and end variables
   * can be changed as such
   */
  React.useEffect(() => {
    setLoading(true);
    getDataViaLines({
      start: 0,
      end: null,
      submissionId: output.analysisSubmissionId,
      fileId: output.id,
    }).then((data) => {
      setFileRows(parseRows(data.lines, data.start, fileExtCSV));
      setFileCols(parseHeader(firstLine, fileExtCSV));
      setLoading(false);
      setTotal(data.lines.length);
    });
  }, []);

  return (
    <div>
      <OutputFileHeader output={output} />
      <TabularOutputWrapper>
        <Table
          layout="auto"
          columns={fileCols}
          loading={loading}
          dataSource={fileRows}
          scroll={{ x: "max-content" }}
          pagination={getPaginationOptions(total)}
        />
      </TabularOutputWrapper>
      <Divider />
    </div>
  );
}
