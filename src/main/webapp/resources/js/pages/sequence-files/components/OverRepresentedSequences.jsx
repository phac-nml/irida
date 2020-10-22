import React, { useEffect, useState } from "react";
import { Layout, Table } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { getFastQCDetails } from "../../../apis/files/sequence-files";


export default function OverRepresentedSequences() {
  const [fastQC, setFastQC] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getFastQCDetails(window.PAGE.seqObjectId, window.PAGE.seqFileId).then(({ analysisFastQC }) => {
      setFastQC(analysisFastQC);
      setLoading(false);
    });
  }, []);

  const columns = [
    {
      title: "Sequence",
      key: "sequence",
      dataIndex: "sequence",
    },
    {
      title: "Percentage",
      key: "percentage",
      dataIndex: "percentage",
    },
    {
      title: "Count",
      key: "overrepresentedSequenceCount",
      dataIndex: "overrepresentedSequenceCount",
    },
    {
      title: "Possible Source",
      key: "possibleSource",
      dataIndex: "possibleSource",
    },

  ]

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent title={`Overrepresented Sequences`}>
        <p>On overrepresented sequences page</p>

        <Table
          bordered
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={fastQC.overrepresentedSequences}
        />
      </TabPaneContent>
    </Layout>
  );
}