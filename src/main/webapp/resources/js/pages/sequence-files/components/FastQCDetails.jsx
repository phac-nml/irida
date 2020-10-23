/*
 * This file renders the FastQC details component.
 */

import React, { useEffect, useState } from "react";
import { Divider, Layout, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { BasicList } from "../../../components/lists";
import { getFastQCDetails } from "../../../apis/files/sequence-files";
import { formatDate } from "../../../utilities/date-utilities";
import { ContentLoading } from "../../../components/loader";
import { seqFileId, seqObjId } from "../fastqc-constants";

export default function FastQCDetails() {
  const [loading, setLoading] = useState(true);
  const [file, setFile] = useState({});
  const [fastQC, setFastQC] = useState({});

  useEffect(() => {
    getFastQCDetails(seqObjId, seqFileId).then(({ sequenceFile, analysisFastQC }) => {
      setFile(sequenceFile);
      setFastQC(analysisFastQC);
      setLoading(false);
    });
  }, []);

  const fileDetails = [
    {
      title: i18n("FastQC.id"),
      desc: file.identifier
    },
    {
      title: i18n("FastQC.uploadedOn"),
      desc: formatDate({ date: file.createdDate })
    },
    {
      title: i18n("FastQC.encoding"),
      desc: fastQC.encoding
    },
  ];

  const sequenceDetails = [
    {
      title: i18n("FastQC.totalSequences"),
      desc: fastQC.totalSequences
    },
    {
      title: i18n("FastQC.totalBases"),
      desc: fastQC.totalBases
    },
    {
      title: i18n("FastQC.minLength"),
      desc: fastQC.minLength
    },
    {
      title: i18n("FastQC.maxLength"),
      desc: fastQC.maxLength
    },
    {
      title: i18n("FastQC.gcContent"),
      desc: fastQC.gcContent
    },
  ];

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent>
        { loading ?
          <div>
            <ContentLoading
              message={i18n("FastQC.fetchingDetails")}
            />
          </div>
          :
          <div>
            <Typography.Title level={4}>{i18n("FastQC.fileDetails")}</Typography.Title>
            <BasicList dataSource={fileDetails} />
            <Divider />
            <Typography.Title level={4}>{i18n("FastQC.sequenceDetails")}</Typography.Title>
            <BasicList dataSource={sequenceDetails} />
          </div>
        }
      </TabPaneContent>
    </Layout>
  );
}