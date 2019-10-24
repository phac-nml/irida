/*
 * This file renders the 'Standard Output' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays the standard output
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { StandardOutput } from "./StandardOutput";
import { Col, Typography } from "antd";
import { getI18N } from "../../../../utilities/i18n-utilties";

const { Title } = Typography;

export default function StandardOutputTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors
}) {
  return (
    <Col span={12}>
      <Title level={3}>{getI18N("AnalysisError.standardError")}</Title>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="standard-out"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
        />
      ) : (
        <StandardOutput galaxyJobErrors={galaxyJobErrors} />
      )}
    </Col>
  );
}
