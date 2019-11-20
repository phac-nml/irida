/**
 * @File component renders a preview of bio hansel output files.
 */

import React, { useEffect, useState } from "react";
import { Button, Col, Divider, Icon, Row, Typography } from "antd";
import { AgGridReact } from "@ag-grid-community/react";
import { AllCommunityModules } from "@ag-grid-community/all-modules";
import { convertFileSize } from "../../../utilities/file.utilities";
import { getDataViaLines } from "../../../apis/analysis/analysis";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";
import { parseHeader, parseRows, autoSizeAll } from "../tabular-preview";
import { SPACE_XS } from "../../../styles/spacing";
import { FONT_SIZE_DEFAULT } from "../../../styles/fonts";

const { Text } = Typography;

export function AnalysisTabularPreview({ output }) {
  const { firstLine, fileExt } = output;
  const fileExtCSV = fileExt === "csv";

  const [fileRows, setFileRows] = useState([]);

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
    });
  }, []);

  return (
    <div>
      <Row>
        <div
          style={{
            marginBottom: SPACE_XS,
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center"
          }}
        >
          <Col>
            <Text
              style={{
                fontSize: FONT_SIZE_DEFAULT
              }}
            >
              {`${output.toolName} ${output.toolVersion} - ${output.outputName} - ${output.filename}`}
            </Text>
          </Col>
          <Col>
            <Button
              style={{
                marginLeft: SPACE_XS
              }}
            >
              <Icon type="download" />
              {`${output.filename} (${convertFileSize(output.fileSizeBytes)})`}
            </Button>
          </Col>
        </div>
      </Row>
      <Row>
        <div
          className="ag-theme-balham"
          style={{ height: "300px", width: "100%", marginBottom: SPACE_XS }}
        >
          <AgGridReact
            columnDefs={parseHeader(firstLine, fileExtCSV)}
            rowData={fileRows}
            modules={AllCommunityModules}
            enableColResize={true}
            maxConcurrentDatasourceRequests={2}
            onGridReady={params => autoSizeAll(params)}
          />
        </div>
        <Divider />
      </Row>
    </div>
  );
}
