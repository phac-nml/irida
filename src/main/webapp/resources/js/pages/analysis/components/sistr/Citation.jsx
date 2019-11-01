/*
 * This file renders the citation component for SISTR
 */

import React from "react";

import { Col, Typography } from "antd";
import { getI18N } from "../../../../utilities/i18n-utilties";

const { Title } = Typography;

export default function Citation() {
  /*
   * Returns the citation for the SISTR workflow
   */
  return (
    <Col span={12}>
      <Title level={2} className="t-page-title">
        {getI18N("AnalysisSistr.citation")}
      </Title>
      <a href="https://doi.org/10.1371/journal.pone.0147101">
        {getI18N("AnalysisSistr.citationLinkText")}
        <cite>{getI18N("AnalysisSistr.plosOne")}</cite>
      </a>
    </Col>
  );
}
