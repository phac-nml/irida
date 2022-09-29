/*
 * This file renders the FastQC details component which
 * lists the file details as well as the sequence details.
 */

import React from "react";
import { Divider, Layout, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPanelContent } from "../../../components/tabs/TabPanelContent";
import { BasicList } from "../../../components/lists";
import { formatDate } from "../../../utilities/date-utilities";
import { ContentLoading } from "../../../components/loader";
import { useFastQCState } from "../fastqc-context";

export default function FastQCDetails() {
  const { loading, file, fastQC } = useFastQCState();

  // List details for file
  const fileDetails = [
    {
      title: i18n("FastQC.id"),
      desc: file.identifier,
      props: {
        className: "t-fastqc-id",
      },
    },
    {
      title: i18n("FastQC.uploadedOn"),
      desc: formatDate({ date: file.createdDate }),
      props: {
        className: "t-fastqc-uploaded-on",
      },
    },
    {
      title: i18n("FastQC.encoding"),
      desc: fastQC.encoding,
      props: {
        className: "t-fastqc-encoding",
      },
    },
  ];

  // List details for sequence
  const sequenceDetails = [
    {
      title: i18n("FastQC.totalSequences"),
      desc: fastQC.totalSequences,
      props: {
        className: "t-fastqc-total-sequences",
      },
    },
    {
      title: i18n("FastQC.totalBases"),
      desc: fastQC.totalBases,
      props: {
        className: "t-fastqc-total-bases",
      },
    },
    {
      title: i18n("FastQC.minLength"),
      desc: fastQC.minLength,
      props: {
        className: "t-fastqc-min-length",
      },
    },
    {
      title: i18n("FastQC.maxLength"),
      desc: fastQC.maxLength,
      props: {
        className: "t-fastqc-max-length",
      },
    },
    {
      title: i18n("FastQC.gcContent"),
      desc: fastQC.gcContent,
      props: {
        className: "t-fastqc-gc-content",
      },
    },
  ];

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPanelContent>
        {loading ? (
          <div>
            <ContentLoading message={i18n("FastQC.fetchingDetails")} />
          </div>
        ) : (
          <div>
            <Typography.Title level={4} className="t-file-details-title">
              {i18n("FastQC.fileDetails")}
            </Typography.Title>
            <BasicList dataSource={fileDetails} />
            <Divider />
            <Typography.Title level={4} className="t-sequence-details-title">
              {i18n("FastQC.sequenceDetails")}
            </Typography.Title>
            <BasicList dataSource={sequenceDetails} />
          </div>
        )}
      </TabPanelContent>
    </Layout>
  );
}
