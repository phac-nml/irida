import React, { useEffect, useState } from "react";
import { Button, Icon, Row, Typography } from "antd";
import { AgGridReact } from "@ag-grid-community/react";
import { AllCommunityModules } from "@ag-grid-community/all-modules";
import { convertFileSize } from "../../../utilities/file.utilities";
import { getFileData } from "../../../apis/analysis/analysis";

import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";

import { parseHeader, parseRows, autoSizeAll } from "../tabular-preview";

const { Text } = Typography;

export function AnalysisTabularPreview({ output }) {
  const { firstLine, fileExt } = output;
  const isCSV = fileExt === "csv";

  const [rows, setRows] = useState([]);

  useEffect(() => {
    getFileData({
      start: 0,
      end: 500,
      submissionId: output.analysisSubmissionId,
      fileId: output.id
    }).then(data => {
      setRows(parseRows(data.lines, data.start, isCSV));
    });
  }, []);

  return (
    <Row style={{ marginBottom: "50px" }}>
      <div
        className="ag-theme-balham"
        style={{ height: "300px", width: "100%" }}
      >
        <div style={{ marginBottom: "5px" }}>
          <Text style={{ fontSize: "14px" }}>
            {`${output.toolName} ${output.toolVersion} - ${output.outputName} - ${output.filename}`}
            <Button type="default" style={{ marginLeft: "5px" }}>
              <Icon type="download" />
              {`${output.filename} (${convertFileSize(output.fileSizeBytes)})`}
            </Button>
          </Text>
        </div>
        <AgGridReact
          columnDefs={parseHeader(firstLine, isCSV)}
          rowData={rows}
          modules={AllCommunityModules}
          paginationPageSize={100}
          enableColResize={true}
          rowBuffer={0}
          cacheOverflowSize={1}
          maxConcurrentDatasourceRequests={2}
          infiniteInitialRowCount={1}
          maxBlocksInCache={undefined}
          onGridReady={params => autoSizeAll(params)}
        />
      </div>
    </Row>
  );
}
