/*
 * This file renders the FastQC charts component.
 */

import React, { useEffect, useState } from "react";
import { Col, Image, Layout , Row, Typography} from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { getFastQCImages } from "../../../apis/files/sequence-files";
import { ContentLoading } from "../../../components/loader";
import {
  seqObjId,
  seqFileId
} from "../fastqc-constants";

export default function FastQCCharts() {

  const [loading, setLoading] = useState(true);
  const [perBase, setPerBase] = useState("");
  const [perSeq, setPerSeq] = useState("");
  const [duplicationLevel, setDuplicationLevel] = useState("");
  const [fastQCVersion, setFastQCVersion] = useState("");

  useEffect(() => {
    getFastQCImages(seqObjId, seqFileId).then(({ perbaseChart, persequenceChart, duplicationlevelChart, fastQCVersion }) => {
      // Convert the images from byte arrays into a png images
      setPerBase(`data:image/png;base64,${perbaseChart}`);
      setPerSeq(`data:image/png;base64,${persequenceChart}`);
      setDuplicationLevel(`data:image/png;base64,${duplicationlevelChart}`);

      setFastQCVersion(fastQCVersion);
      setLoading(false);
    });
  }, []);

  return (
    <div>
    {
      loading ?
        <ContentLoading message={i18n("FastQC.fetchingCharts")}/>
        :
        <Layout style={{paddingLeft: SPACE_MD, backgroundColor: grey1}}>
          <TabPaneContent title={i18n("FastQC.charts")}>
            <Typography.Paragraph>{i18n("FastQC.overrepresentedSequencesDescription", fastQCVersion)}</Typography.Paragraph>
            <Row
              gutter={[16, 16]}
              style={{ padding: SPACE_MD }}
            >
              <Col sm={24} md={12}>
                <Image src={perBase} />
              </Col>
              <Col sm={24} md={12}>
                <Image src={perSeq} />
              </Col>
              <Col sm={24} md={12}>
                <Image src={duplicationLevel} />
              </Col>
            </Row>
          </TabPaneContent>
        </Layout>
    }
    </div>
  );
}