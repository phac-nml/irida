/**
 * @File component renders a preview of excel output files.
 */

import React from "react";
import { Divider, Radio, Table, Typography } from "antd";
import { SPACE_XS } from "../../../styles/spacing";
import styled from "styled-components";
import { OutputFileHeader } from "../../../components/OutputFiles";
import { parseExcel } from "../../../apis/analysis/analysis";
import { WarningAlert } from "../../../components/alerts";
import { ContentLoading } from "../../../components/loader";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";

const { Text } = Typography;

const ExcelOutputWrapper = styled.div`
  max-height: 300px;
  width: 100%;
  margin-bottom: ${SPACE_XS};
`;

export default function AnalysisExcelPreview({ output }) {
  const [excelHeaders, setExcelHeaders] = React.useState([]);
  const [excelSheetNames, setExcelSheetNames] = React.useState([]);
  const [currSheetIndex, setCurrSheetIndex] = React.useState("0");
  const [parseError, setParseError] = React.useState(null);
  const [rowData, setRowData] = React.useState([]);

  const [total, setTotal] = React.useState(0);

  React.useEffect(() => {
    parseExcel(
      output.analysisSubmissionId,
      output.filename,
      currSheetIndex
    ).then(({ data }) => {
      if (!data.parseError) {
        setExcelHeaders(data.excelHeaders);
        getFormattedRowData(data.excelRows);
        setExcelSheetNames(data.excelSheetNames);
        setParseError(data.parseError);
      } else {
        setParseError(true);
      }
    });
  }, [currSheetIndex]);

  /* This function is used to format row data
   * to the appropriate format that the antd
   * table is expecting.
   */
  function getFormattedRowData(excelRows) {
    let formattedRowData = [];
    if (excelRows.length > 0) {
      setTotal(excelRows.length);
      for (const [i, row] of excelRows.entries()) {
        const excelHeaderColumns = row.excelCols;
        let columnData = {};
        for (const [j, col] of excelHeaderColumns.entries()) {
          columnData[j] = col.value;
        }
        columnData["index"] = i + 1;
        columnData["key"] = i + 1;
        formattedRowData.push(columnData);
      }
      setRowData(formattedRowData);
    }
  }

  // Set the index of the excel sheet selected
  function changeExcelSheet(e) {
    setCurrSheetIndex(e.target.value);
  }

  // Displays the sheets as radio group buttons
  function displaySheetNames() {
    const sheetNames = [];

    for (const [i, sheetName] of excelSheetNames.entries()) {
      sheetNames.push(
        <Radio.Button value={`${i}`} key={`sheet-button-${i + 1}`}>
          {sheetName}
        </Radio.Button>
      );
    }

    return (
      <Radio.Group
        value={currSheetIndex}
        onChange={changeExcelSheet}
        style={{ marginBottom: SPACE_XS }}
      >
        <div>
          <Text strong>Sheets:</Text>
          <span style={{ marginLeft: SPACE_XS }}>{sheetNames}</span>
        </div>
      </Radio.Group>
    );
  }

  /*
   * Displays the excel output in an antd table component if there
   * was no error while parsing the excel file
   */
  function displayExcelOutput() {
    return (
      <div>
        <OutputFileHeader output={output} />
        {displaySheetNames()}
        <ExcelOutputWrapper>
          <Table
            layout="auto"
            columns={excelHeaders}
            dataSource={rowData}
            scroll={{ x: "max-content" }}
            pagination={getPaginationOptions(total)}
          />
        </ExcelOutputWrapper>
        <Divider />
      </div>
    );
  }

  return (
    <>
      {parseError != null ? (
        !parseError ? (
          displayExcelOutput()
        ) : (
          <WarningAlert message={i18n("AnalysisOutputs.excelParsingError")} />
        )
      ) : (
        <ContentLoading />
      )}
    </>
  );
}
