import React, { useEffect, useState } from "react";
import { Divider, Layout, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { BasicList } from "../../../components/lists";
import { getFastQCDetails } from "../../../apis/files/sequence-files";
import { formatDate } from "../../../utilities/date-utilities";
import { ContentLoading } from "../../../components/loader";

export default function FastQCDetails() {
  const [loading, setLoading] = useState(true);
  const [file, setFile] = useState({});
  const [seqObject, setSeqObject] = useState({});
  const [fastQC, setFastQC] = useState({});

  useEffect(() => {
    getFastQCDetails(window.PAGE.seqObjectId, window.PAGE.seqFileId).then(({ sequenceFile, sequencingObject, analysisFastQC }) => {
      setFile(sequenceFile);
      setSeqObject(sequencingObject);
      setFastQC(analysisFastQC);
      setLoading(false);
    });
  }, []);

  const fileDetails = [
    {
      title: "ID",
      desc: file.identifier
    },
    {
      title: "Uploaded On",
      desc: formatDate({ date: file.createdDate })
    },
    {
      title: "Encoding",
      desc: fastQC.encoding
    },
  ];

  const sequenceDetails = [
    {
      title: "Total Sequences",
      desc: fastQC.totalSequences
    },
    {
      title: "Total Bases",
      desc: fastQC.totalBases
    },
    {
      title: "Min. Length",
      desc: fastQC.minLength
    },
    {
      title: "Max. Length",
      desc: fastQC.maxLength
    },
    {
      title: "GC Content",
      desc: fastQC.gcContent
    },
  ];

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent>
        { loading ?
          <div>
            <ContentLoading
              message="Fetching fastqc details"
            />
          </div>
          :
          !fastQC ?
              <p>There is no FastQC data available for this file.</p>
            :
            <div>
              <Typography.Title level={4}>File Details</Typography.Title>
              <BasicList dataSource={fileDetails} />
              <Divider />
              <Typography.Title level={4}>Sequence Details</Typography.Title>
              <BasicList dataSource={sequenceDetails} />
            </div>
        }
      </TabPaneContent>
    </Layout>
  );
}