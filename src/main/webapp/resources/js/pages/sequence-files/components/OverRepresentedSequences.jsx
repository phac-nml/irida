/*
 * This file renders the OverRepresentedSequences component
 * which is a table.
 */

import React from "react";
import { Layout, Table, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { Monospace } from "../../../components/typography";
import { useFastQCDispatch, useFastQCState } from "../fastqc-context";

export default function OverRepresentedSequences() {
  const { loading, analysisFastQC } = useFastQCState();
  const { getOverrepresentedDetails } = useFastQCDispatch();

  React.useEffect(() => {
    if (!analysisFastQC) {
      getOverrepresentedDetails();
    }
  }, [getOverrepresentedDetails]);

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

  return analysisFastQC ? (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent title={i18n("FastQC.overrepresentedSequences")}>
        <Typography.Paragraph className="text-info">
          {analysisFastQC.description}
        </Typography.Paragraph>
        <Table
          bordered
          pagination={false}
          rowKey={(item) => item.identifier}
          loading={loading}
          columns={columns}
          dataSource={analysisFastQC.overrepresentedSequences}
          className="t-overrepresented-sequences-table"
        />
      </TabPaneContent>
    </Layout>
  ) : null;
}
