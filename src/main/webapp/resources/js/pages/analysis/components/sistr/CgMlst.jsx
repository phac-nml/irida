/*
 * This file renders the cgMLST component for SISTR
 */

import React from "react";
import { BasicList } from "../../../../components/lists/BasicList";
import { Col, Typography } from "antd";
import { getI18N } from "../../../../utilities/i18n-utilties";

const { Title } = Typography;

export default function CgMlst({ sistrResults }) {
  function cgMLST330() {
    return [
      {
        title: getI18N("AnalysisSistr.subspecies"),
        desc: sistrResults.cgmlst_subspecies
      },
      {
        title: getI18N("AnalysisSistr.matchingGenomeName"),
        desc: sistrResults.cgmlst_genome_match
      },
      {
        title: getI18N("AnalysisSistr.allelesMatchingGenome"),
        desc: `${sistrResults.cgmlst_matching_alleles}/330`
      },
      {
        title: getI18N("AnalysisSistr.percentMatching"),
        desc: `${getPercentage(sistrResults.cgmlst_distance).toString()}%`
      },
      {
        title: getI18N("AnalysisSistr.cgmlstSequenceType"),
        desc: sistrResults.cgmlst_ST.toString()
      }
    ];
  }

  function getPercentage(str) {
    return parseFloat((1 - str) * 100).toFixed(1);
  }

  /*
   * Returns a simple list which displays labels and values
   * for the cgMLST data
   */
  return (
    <Col span={12}>
      <Title level={2} className="t-page-title">
        {getI18N("AnalysisSistr.cgmlst330")}
      </Title>
      <BasicList dataSource={cgMLST330()} />
    </Col>
  );
}
