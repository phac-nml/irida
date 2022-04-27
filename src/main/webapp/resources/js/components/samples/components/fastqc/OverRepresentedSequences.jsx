/*
 * This file renders the OverRepresentedSequences component
 * which is a table.
 */

import React from "react";
import { Col, Row, Table, Typography } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { Monospace } from "../../../typography";
import { getOverRepresentedSequences } from "../../../../apis/files/sequence-files";
import { setAnalysisFastQC } from "./fastQCSlice";
import { getPaginationOptions } from "../../../../utilities/antdesign-table-utilities";

const DEFAULT_HEIGHT = 600;

export default function OverRepresentedSequences() {
  const dispatch = useDispatch();
  const { loading, sequencingObjectId, fileId, fastQC } = useSelector(
    (state) => state.fastQCReducer
  );

  React.useEffect(() => {
    if (Object.keys(fastQC).length === 0) {
      getOverRepresentedSequences(sequencingObjectId, fileId).then(
        (analysisFastQC) => {
          dispatch(
            setAnalysisFastQC({
              fastQC: analysisFastQC,
            })
          );
        }
      );
    }
  }, [sequencingObjectId, fileId]);

  // Columns for the table
  const columns = [
    {
      title: i18n("FastQC.overrepresented.sequence"),
      key: "sequence",
      dataIndex: "sequence",
      render(data) {
        // Display sequence in monospace font
        return <Monospace>{data}</Monospace>;
      },
    },
    {
      title: i18n("FastQC.overrepresented.percentage"),
      key: "percentage",
      dataIndex: "percentage",
      render(data) {
        // Round to the nearest 10th and display 1 decimal point
        return `${(Math.round(data * 10) / 10).toFixed(1)} %`;
      },
    },
    {
      title: i18n("FastQC.overrepresented.count"),
      key: "overrepresentedSequenceCount",
      dataIndex: "overrepresentedSequenceCount",
    },
    {
      title: i18n("FastQC.overrepresented.possibleSource"),
      key: "possibleSource",
      dataIndex: "possibleSource",
    },
  ];

  return fastQC ? (
    <Row gutter={16}>
      <Col
        span={24}
        style={{ height: DEFAULT_HEIGHT }}
      >
        <Typography.Paragraph className="text-info">
          {fastQC.description}
        </Typography.Paragraph>
        <Table
          bordered
          pagination={false}
          rowKey={(item) => item.identifier}
          loading={loading}
          columns={columns}
          dataSource={fastQC.overrepresentedSequences}
          className="t-overrepresented-sequences-table"
        />
      </Col>
    </Row>
  ) : null;
}
