/**
 * @File component renders a preview of excel output files.
 */

import React, { useEffect, useState } from "react";
import { Divider, Table } from "antd";
import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";
import { OutputFileHeader } from "../../../components/OutputFiles";
import { parseExcel } from "../../../apis/analysis/analysis";

const ExcelOutputWrapper = styled.div`
  max-height: 300px;
  width: 100%;
  margin-bottom: ${SPACE_XS};
`;

export default function AnalysisExcelPreview({ output }) {
  const [excelCols, setExcelCols] = useState([]);
  const [excelRows, setExcelRows] = useState([]);

  useEffect(() => {
    parseExcel(output.analysisSubmissionId, output.filename).then(data => {
      setExcelCols(data["data"].excelHeaders);
      setExcelRows(data["data"].excelRows);
    });
  }, []);

  /* This function is used to format row data
   * to the appropriate format that the antd
   * table is expecting.
   */
  function formatRowData() {
    let rowData = [];
    if(excelRows.length > 0) {
      for(let i = 0; i<excelRows.length; i++) {
        let excelColumns = excelRows[i].excelCols;
        let columnData = {};
        for(let j = 0; j<excelColumns.length; j++) {
          columnData[j] = excelColumns[j].value;
        }
        columnData["index"] = i+1;
        columnData["key"] = i+1;
        rowData.push(columnData);
      }
    }
    return rowData;
  }

  /*
   * Displays the excel output in an antd table component
   */
  function displayExcelOutput() {
      return (
        <div>
          <OutputFileHeader output={output} />
          <ExcelOutputWrapper>
            <Table
              layout="auto"
              columns={excelCols}
              dataSource={formatRowData()}
              scroll={{ x: "max-content" }}
              pagination={false}
            />
          </ExcelOutputWrapper>
          <Divider />
        </div>
      );
  }

  return <>{displayExcelOutput()}</>;
}
