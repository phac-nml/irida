/*
 * This file renders the Mash component for SISTR
 */

import React from "react";
import { BasicList } from "../../../../components/lists/BasicList";
import { Col, Typography } from "antd";
import { getI18N } from "../../../../utilities/i18n-utilties";

const { Title } = Typography;

export default function Mash({ sistrResults }) {
  function mash() {
    return [
      {
        title: getI18N("AnalysisSistr.subspecies"),
        desc: sistrResults.mash_subspecies
      },
      {
        title: getI18N("AnalysisSistr.serovar"),
        desc: sistrResults.mash_serovar
      },
      {
        title: getI18N("AnalysisSistr.matchingGenomeName"),
        desc: sistrResults.mash_genome
      },
      {
        title: getI18N("AnalysisSistr.mashDistance"),
        desc: sistrResults.mash_distance.toString()
      }
    ];
  }

  /*
   * Returns a simple list which displays labels and values
   * for the sistr mash data
   */
  return (
    <Col span={12}>
      <Title level={2} className="t-page-title">
        {getI18N("AnalysisSistr.mash")}
      </Title>
      <BasicList dataSource={mash()} />
    </Col>
  );
}
