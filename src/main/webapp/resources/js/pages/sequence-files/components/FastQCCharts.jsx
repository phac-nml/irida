/*
 * This file renders the FastQC charts component.
 */

import { Col, Layout, Row, Typography } from "antd";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import styled from "styled-components";
import { getFastQCImages } from "../../../apis/files/sequence-files";
import { ContentLoading } from "../../../components/loader";
import { TabPanelContent } from "../../../components/tabs/TabPanelContent";
import { grey1, grey4 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";

const StyledImage = styled.img`
  height: 100%;
  width: 100%;
  border: 1px solid ${grey4};
  padding: ${SPACE_MD};
  border-radius: 2px;
`;

export default function FastQCCharts() {
  const { sequenceObjectId, fileId } = useParams();

  const [loading, setLoading] = useState(true);
  const [perBase, setPerBase] = useState("");
  const [perSeq, setPerSeq] = useState("");
  const [duplicationLevel, setDuplicationLevel] = useState("");
  const [fastQCVersion, setFastQCVersion] = useState("");

  useEffect(() => {
    getFastQCImages(sequenceObjectId, fileId).then(
      ({
        perbaseChart,
        persequenceChart,
        duplicationlevelChart,
        fastQCVersion,
      }) => {
        // Convert the images from byte arrays into a png images
        setPerBase(`data:image/png;base64,${perbaseChart}`);
        setPerSeq(`data:image/png;base64,${persequenceChart}`);
        setDuplicationLevel(`data:image/png;base64,${duplicationlevelChart}`);

        setFastQCVersion(fastQCVersion);
        setLoading(false);
      }
    );
  }, [fileId, sequenceObjectId]);

  return (
    <div>
      {loading ? (
        <ContentLoading message={i18n("FastQC.fetchingCharts")} />
      ) : (
        <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
          <TabPanelContent title={i18n("FastQC.charts")} xxl={16}>
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
          </TabPanelContent>
        </Layout>
      )}
    </div>
  );
}
