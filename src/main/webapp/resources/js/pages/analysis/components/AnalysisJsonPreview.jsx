/**
 * @File component renders a preview of bio hansel output files.
 */

import React, { useEffect, useState } from "react";
import { Button, Col, Divider, Icon, Row, Typography } from "antd";
import {
  getDataViaChunks,
  downloadOutputFiles
} from "../../../apis/analysis/analysis";
import { convertFileSize } from "../../../utilities/file.utilities";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import {
  getNewChunkSize,
  repairMalformedJSON,
  statusText
} from "../../analysis/json-preview";
import { BasicList } from "../../../components/lists/BasicList";
import { SPACE_XS, SPACE_MD } from "../../../styles/spacing";
import { FONT_SIZE_DEFAULT } from "../../../styles/fonts";
import styled from "styled-components";
import { isAdmin } from "../../../contexts/AnalysisContext";

const { Text } = Typography;

const JsonOutputWrapper = styled.div`
  height: 300px;
  width: 100%;
  overflow: auto;
  margin-bottom: ${SPACE_XS};
  border: solid 1px #bdc3c7;
`;

export function AnalysisJsonPreview({ output }) {
  const [fileRows, setFileRows] = useState([]);
  const chunkSize = 3096;
  let savedText = "";
  let filePointer = 0;

  /*
   * Get json file output data and set the fileRows local state to this data.
   */
  useEffect(() => {
    getDataViaChunks({
      submissionId: output.analysisSubmissionId,
      fileId: output.id,
      seek: 0,
      chunk: getNewChunkSize(0, output.fileSizeBytes, chunkSize)
    }).then(data => {
      savedText = data.text;
      filePointer = data.filePointer;
      try {
        setFileRows(JSON.parse(savedText));
        document.getElementById(
          `${output.filename}-preview-status`
        ).innerText = statusText(data.filePointer, output.fileSizeBytes);
      } catch (e) {
        try {
          console.log("Repairing malformed json");
          setFileRows(repairMalformedJSON(savedText));
          document.getElementById(
            `${output.filename}-preview-status`
          ).innerText = statusText(data.filePointer, output.fileSizeBytes);
        } catch (ee) {
          console.warn(savedText.substr(savedText.length - 100));
          console.error(ee);
        }
      }
    });
  }, []);

  function displayJson() {
    if (fileRows.length > 0) {
      let jsonData = [];

      Object.keys(fileRows).map((key, val) => {
        Object.entries(fileRows[val]).map(fileRowData => {
          jsonData.push({
            title: fileRowData[0],
            desc: fileRowData[1] !== null ? fileRowData[1].toString() : ""
          });
        });
      });
      return (
        <div>
          <Row>
            <div
              style={{
                marginBottom: SPACE_MD,
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
                  onClick={() =>
                    downloadOutputFiles(output.analysisSubmissionId, output.id)
                  }
                >
                  <Icon type="download" />
                  {`${output.filename} (${convertFileSize(
                    output.fileSizeBytes
                  )})`}
                </Button>
              </Col>
            </div>
          </Row>
          {isAdmin ? (
            <Row>
              <JsonOutputWrapper
                id={`json-${output.filename.split(".")[0].replace(/_/g, "-")}`}
                style={{ padding: SPACE_XS }}
              >
                <BasicList dataSource={jsonData} />
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

  return <>{fileRows !== null ? displayJson() : <ContentLoading />}</>;
}
