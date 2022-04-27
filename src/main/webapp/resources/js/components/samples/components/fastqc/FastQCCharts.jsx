/*
 * This file renders the FastQC charts component.
 */

import { Col, Image, Row, Typography } from "antd";
import React, { useState } from "react";
import { useSelector } from "react-redux";
import styled from "styled-components";

import { getFastQCImages } from "../../../../apis/files/sequence-files";
import { ContentLoading } from "../../../loader";
import { grey4 } from "../../../../styles/colors";
import { SPACE_MD } from "../../../../styles/spacing";

const StyledImage = styled(Image)`
  border: 1px solid ${grey4};
  padding: ${SPACE_MD};
  border-radius: 2px;
`;

const DEFAULT_HEIGHT = 600;

export default function FastQCCharts() {
  const [loading, setLoading] = useState(true);
  const [perBase, setPerBase] = useState("");
  const [perSeq, setPerSeq] = useState("");
  const [duplicationLevel, setDuplicationLevel] = useState("");
  const [fastQCVersion, setFastQCVersion] = useState("");

  const { sequencingObjectId, fileId } = useSelector(
    (state) => state.fastQCReducer
  );

  React.useEffect(() => {
    getFastQCImages(sequencingObjectId, fileId).then(
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
  }, [fileId, sequencingObjectId]);

  return (
    <>
      {loading ? (
        <ContentLoading message={i18n("FastQC.fetchingCharts")} />
      ) : (
        <div>
          <Typography.Paragraph>
            {i18n("FastQC.overrepresentedSequencesDescription", fastQCVersion)}
          </Typography.Paragraph>
          <Row gutter={[16, 16]} style={{ padding: SPACE_MD, height: DEFAULT_HEIGHT }}>
            <Col span={12}>
              <StyledImage
                src={perBase}
                className="t-sequenceFile-qc-chart"
                alt={i18n("FastQC.chart.perbase")}
              />
            </Col>
            <Col span={12}>
              <StyledImage
                src={perSeq}
                className="t-sequenceFile-qc-chart"
                alt={i18n("FastQC.chart.persequence")}
              />
            </Col>
            <Col span={12}>
              <StyledImage
                src={duplicationLevel}
                className="t-sequenceFile-qc-chart"
                alt={i18n("FastQC.chart.duplicationlevel")}
              />
            </Col>
          </Row>
        </div>
      )}
    </>
  );
}
