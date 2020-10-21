import React, { useEffect, useState } from "react";
import { Divider, Layout, notification, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { BasicList } from "../../../components/lists";
import { getFastQCDetails } from "../../../apis/files/sequence-files";

export default function FastQCDetails() {
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState([]);

  useEffect(() => {
    getFastQCDetails(window.PAGE.seqObjectId, window.PAGE.seqFileId).then(res => {
      setData(res);
      setLoading(false);
    });
  }, []);

  const fileDetails = [
    {
      title: "ID",
      desc: 44
    },
    {
      title: "Uploaded On",
      desc: "October 21, 2020"
    },
    {
      title: "Encoding",
      desc: "Sanger / Illumina 1.9"
    },
  ];

  const sequenceDetails = [
    {
      title: "Total Sequences",
      desc: 270
    },
    {
      title: "Total Bases",
      desc: 67500
    },
    {
      title: "Min. Length",
      desc: 250
    },
    {
      title: "Max. Length",
      desc: 250
    },
    {
      title: "GC Content",
      desc: 36
    },
  ];

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent>
        <Typography.Title level={4}>File Details</Typography.Title>
        <BasicList dataSource={fileDetails} />
        <Divider />
        <Typography.Title level={4}>Sequence Details</Typography.Title>
        <BasicList dataSource={sequenceDetails} />
      </TabPaneContent>
    </Layout>
  );
}