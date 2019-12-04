/**
 * @File component renders a JSON preview of output files.
 */

import React, { useEffect, useState } from "react";
import { Divider, List, Row, Typography } from "antd";
import { getDataViaChunks } from "../../../apis/analysis/analysis";
import { isAdmin } from "../../../contexts/AnalysisContext";
import { OutputFileHeader } from "../../../components/OutputFiles/OutputFileHeader";
import { grey4 } from "../../../styles/colors";
import { JsonOutputWrapper } from "../../../components/OutputFiles/JsonOutputWrapper";
import { JsonObjectOutputWrapper } from "../../../components/OutputFiles/JsonObjectOutputWrapper";

import AutoSizer from "react-virtualized-auto-sizer";
import { FixedSizeList as VList } from "react-window";

const { Text } = Typography;
const SCROLLABLE_DIV_HEIGHT = 300;

export default function AnalysisJsonPreview({ output }) {
  let savedText = "";
  const [jsonData, setJsonData] = useState(null);
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

      if (Array.isArray(parsedJson)) {
        Object.keys(parsedJson).map((key, val) => {
          if (parsedJson[val] !== undefined) {
            Object.entries(parsedJson[val]).map(fileRowData => {
              jsonListData.push({
                title: fileRowData[0],
                desc: fileRowData[1] !== null ? fileRowData[1].toString() : ""
              });
            });
          }
        });
        setJsonData(jsonListData);
      } else {
        setJsonData(parsedJson);
      }
    });
  }, []);

  function renderListItem({ index, style }) {
    const item = jsonData[index];
    return (
      <List.Item
        key={index}
        style={{ ...style, borderBottom: `solid 1px ${grey4}` }}
      >
        <List.Item.Meta
          title={item.title}
          description={item.desc}
        ></List.Item.Meta>
      </List.Item>
    );
  }

  function getString() {
    return JSON.stringify(jsonData, null, 2);
  }

  return jsonData !== null ? (
    <div>
      <Row>
        <OutputFileHeader output={output} />
      </Row>
      {isAdmin ? (
        <Row>
          {Array.isArray(jsonData) ? (
            <JsonOutputWrapper>
              <AutoSizer>
                {({ height = SCROLLABLE_DIV_HEIGHT, width = "100%" }) => (
                  <VList
                    itemCount={jsonData.length}
                    itemSize={70}
                    height={height}
                    width={width}
                  >
                    {renderListItem}
                  </VList>
                )}
              </AutoSizer>
            </JsonOutputWrapper>
          ) : (
            <JsonObjectOutputWrapper>
              <Text>{getString()}</Text>
            </JsonObjectOutputWrapper>
          )}

          <Divider />
        </Row>
      ) : null}
    </div>
  ) : null;
}
