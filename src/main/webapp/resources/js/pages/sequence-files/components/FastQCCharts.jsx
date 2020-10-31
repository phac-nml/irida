/*
 * This file renders the FastQC charts component.
 */

import React from "react";
import { Col, Layout, Row, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1, grey4 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { ContentLoading } from "../../../components/loader";

import styled from "styled-components";
import { useFastQCDispatch, useFastQCState } from "../fastqc-context";

const StyledImage = styled.img`
  height: 100%;
  width: 100%;
  border: 1px solid ${grey4};
  padding: ${SPACE_MD};
  border-radius: 2px;
`;

export default function FastQCCharts() {
  const {
    loading,
    perBase,
    perSeq,
    duplicationLevel,
    fastQCVersion,
  } = useFastQCState();
  const { dispatchGetFastQCImages } = useFastQCDispatch();

  React.useEffect(() => {
    if (!perBase) {
      dispatchGetFastQCImages();
    }
  }, [dispatchGetFastQCImages, perBase]);

  return (
    <div>
      {loading ? (
        <ContentLoading message={i18n("FastQC.fetchingCharts")} />
      ) : (
        <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
          <TabPaneContent title={i18n("FastQC.charts")} xxl={16}>
            <Typography.Paragraph>
              {i18n(
                "FastQC.overrepresentedSequencesDescription",
                fastQCVersion
              )}
            </Typography.Paragraph>
            <Row gutter={[16, 16]} style={{ padding: SPACE_MD }}>
              <Col span={24} xl={16} xxl={12}>
                <StyledImage
                  src={perBase}
                  className="t-sequenceFile-qc-chart"
                  alt={i18n("FastQC.chart.perbase")}
                />
              </Col>
              <Col span={24} xl={16} xxl={12}>
                <StyledImage
                  src={perSeq}
                  className="t-sequenceFile-qc-chart"
                  alt={i18n("FastQC.chart.persequence")}
                />
              </Col>
              <Col span={24} xl={16} xxl={12}>
                <StyledImage
                  src={duplicationLevel}
                  className="t-sequenceFile-qc-chart"
                  alt={i18n("FastQC.chart.duplicationlevel")}
                />
              </Col>
            </Row>
          </TabPaneContent>
        </Layout>
      )}
    </div>
  );
}
