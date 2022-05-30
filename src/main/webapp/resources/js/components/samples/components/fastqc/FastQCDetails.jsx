/*
 * This file renders the FastQC details component which
 * lists the file details as well as the sequence details.
 */

import React from "react";
import { Col, List, Row } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { formatDate } from "../../../../utilities/date-utilities";
import { ContentLoading } from "../../../loader";
import { getFastQCDetails } from "../../../../apis/files/sequence-files";
import { setFastQCDetails } from "./fastQCSlice";
import AutoSizer from "react-virtualized-auto-sizer";
import { FixedSizeList as VList } from "react-window";

const DEFAULT_HEIGHT = 600;

export function FastQCDetails() {
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

  const renderDetailsListItem = ({ index, style }) => {
    const item = fileDetails[index];

    return (
      <List.Item style={{ ...style, padding: 15 }}>
        <List.Item.Meta
          title={item.title}
          description={item.desc}
          className={item.props.className}
        />
      </List.Item>
    );
  };

  return (
    <>
      {loading ? (
        <div>
          <ContentLoading message={i18n("FastQC.fetchingDetails")} />
        </div>
      ) : (
        <Row gutter={16}>
          <Col
            span={24}
            style={{
              height: DEFAULT_HEIGHT,
            }}
          >
            <AutoSizer>
              {({ height = DEFAULT_HEIGHT, width = "100%" }) => (
                <VList
                  itemCount={fileDetails.length}
                  itemSize={70}
                  height={height}
                  width={width}
                >
                  {renderDetailsListItem}
                </VList>
              )}
            </AutoSizer>
          </Col>
        </Row>
      )}
    </>
  );
}
