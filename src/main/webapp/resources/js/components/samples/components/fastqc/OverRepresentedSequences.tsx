import React from "react";
import { Col, Row, Table, Typography } from "antd";
import { useAppSelector } from "../../../../hooks/useState";
import { Monospace } from "../../../typography";
import { getPaginationOptions } from "../../../../utilities/antdesign-table-utilities";

const DEFAULT_HEIGHT = 600;

/**
 * React component to render FastQC overrepresented sequences
 * @returns {JSX.Element}
 * @constructor
 */
export function OverRepresentedSequences() {
  const { loading, fastQC } = useAppSelector((state) => state.fastQCReducer);
  const paginationOptions = React.useMemo(
    () =>
      getPaginationOptions(
        Object.keys(fastQC).length > 0 &&
          typeof fastQC.overrepresentedSequences !== "undefined"
          ? fastQC.overrepresentedSequences.length
          : 0
      ),
    [fastQC]
  );

  // Columns for the table
  const columns = [
    {
      title: i18n("FastQC.overrepresented.sequence"),
      key: "sequence",
      dataIndex: "sequence",
      render(data: string) {
        // Display sequence in monospace font
        return <Monospace>{data}</Monospace>;
      },
    },
    {
      title: i18n("FastQC.overrepresented.percentage"),
      key: "percentage",
      dataIndex: "percentage",
      render(data: number) {
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

  return (
    <Row gutter={16} style={{ padding: 10 }}>
      <Col span={24}>
        <Typography.Paragraph className="text-info">
          {fastQC.description}
        </Typography.Paragraph>
      </Col>
      <Col
        span={24}
        style={{
          maxHeight: DEFAULT_HEIGHT,
          overflowY: "auto",
        }}
      >
        <Table
          bordered
          pagination={{
            ...paginationOptions,
            showTotal: (total) =>
              i18n("FastQC.overrepresented.sequences.total", total),
          }}
          rowKey={(item) => item.identifier}
          loading={loading}
          columns={columns}
          dataSource={fastQC.overrepresentedSequences}
          className="t-overrepresented-sequences-table"
        />
      </Col>
    </Row>
  );
}
