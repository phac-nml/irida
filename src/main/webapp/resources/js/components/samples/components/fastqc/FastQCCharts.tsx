import { Col, Image, Row, Typography } from "antd";
import React, { useState } from "react";
import { useAppSelector } from "../../../../hooks/useState";
import styled from "styled-components";

import { getFastQCImages } from "../../../../apis/files/sequence-files";
import { SPACE_MD } from "../../../../styles/spacing";

const StyledImage = styled(Image)`
  border: 1px solid var(--grey-4);
  padding: ${SPACE_MD};
  border-radius: 2px;
`;

/**
 * React component to render FastQC charts
 * @returns {JSX.Element}
 * @constructor
 */
export function FastQCCharts() {
  const [perBase, setPerBase] = useState("");
  const [perSeq, setPerSeq] = useState("");
  const [duplicationLevel, setDuplicationLevel] = useState("");
  const [fastQCVersion, setFastQCVersion] = useState("");

  const { sequencingObjectId, fileId } = useAppSelector(
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
      }
    );
  }, [fileId, sequencingObjectId]);

  return (
    <Row gutter={[16, 16]} style={{ padding: 10 }}>
      <Col span={24}>
        <Typography.Paragraph>
          {i18n("FastQC.overrepresentedSequencesDescription", fastQCVersion)}
        </Typography.Paragraph>
      </Col>
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
  );
}
