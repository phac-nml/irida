/*
 * This file renders the 'Standard Error' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays the standard error
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { StandardError } from "./StandardError";
import { Col, Typography } from "antd";
import { getI18N } from "../../../../utilities/i18n-utilties";

const { Title } = Typography;

export default function StandardErrorTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors
}) {
  return (
    <Col span={12}>
      <Title level={3}>{getI18N("AnalysisError.standardError")}</Title>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="standard-error"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
        />
      ) : (
        <StandardError galaxyJobErrors={galaxyJobErrors} />
      )}
    </Col>
  );
}
