/*
 * This file renders the FastQC details component which
 * lists the file details as well as the sequence details.
 */

import React from "react";
import { Col, Divider, Row, Typography } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { BasicList } from "../../../lists";
import { formatDate } from "../../../../utilities/date-utilities";
import { ContentLoading } from "../../../loader";
import { getFastQCDetails } from "../../../../apis/files/sequence-files";
import { setFastQCDetails } from "./fastQCSlice";

export default function FastQCDetails() {
  const { loading, sequencingObjectId, fileId, file, fastQC } = useSelector(
    (state) => state.fastQCReducer
  );
  const dispatch = useDispatch();

  React.useEffect(() => {
    getFastQCDetails(sequencingObjectId, fileId).then(
      ({ analysisFastQC, sequenceFile, sequencingObject }) => {
        dispatch(
          setFastQCDetails({
            fastQC: analysisFastQC,
            file: sequenceFile,
            processingState: sequencingObject.processingState,
          })
        );
      }
    );
  }, [sequencingObjectId, fileId]);

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
    <>
      {loading ? (
        <div>
          <ContentLoading message={i18n("FastQC.fetchingDetails")} />
        </div>
      ) : (
        <Row gutter={16}>
          <Col span={24}>
            <Typography.Title level={5} className="t-file-details-title">
              {i18n("FastQC.fileDetails")}
            </Typography.Title>
          </Col>
          <Col span={24}>
            <BasicList dataSource={fileDetails} />
          </Col>

          <Divider />
          <Col span={24}>
            <Typography.Title level={5} className="t-sequence-details-title">
              {i18n("FastQC.sequenceDetails")}
            </Typography.Title>
          </Col>
          <Col span={24}>
            <BasicList dataSource={sequenceDetails} />
          </Col>
        </Row>
      )}
    </>
  );
}
