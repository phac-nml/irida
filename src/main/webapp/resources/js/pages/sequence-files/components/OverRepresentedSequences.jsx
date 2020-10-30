/*
 * This file renders the OverRepresentedSequences component
 * which is a table.
 */

import React, { useEffect, useState } from "react";
import { Layout, Table, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { getOverRepresentedSequences } from "../../../apis/files/sequence-files";
import { Monospace } from "../../../components/typography";
import { useParams } from "@reach/router";

export default function OverRepresentedSequences() {
  const { sequenceFileId, fileId } = useParams();

  const [fastQC, setFastQC] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Get the overrepresented sequences and set the fastQC state
    getOverRepresentedSequences(sequenceFileId, fileId).then(
      (analysisFastQC) => {
        setFastQC(analysisFastQC);
        setLoading(false);
      }
    );
  }, []);

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

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent title={i18n("FastQC.overrepresentedSequences")}>
        <Typography.Paragraph className="text-info">
          {fastQC.description}
        </Typography.Paragraph>
        <Table
          bordered
          rowKey={(item) => item.identifier}
          loading={loading}
          columns={columns}
          dataSource={fastQC.overrepresentedSequences}
          className="t-overrepresented-sequences-table"
        />
      </TabPaneContent>
    </Layout>
  );
}
