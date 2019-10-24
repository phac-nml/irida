/*
 * This file renders the 'Galaxy Parameters' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays these parameters
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { GalaxyParameters } from "./GalaxyParameters";
import { Col, Typography } from "antd";
import { getI18N } from "../../../../utilities/i18n-utilties";

const { Title } = Typography;

export default function GalaxyParametersTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors
}) {
  return (
    <Col span={12}>
      <Title level={3}>{getI18N("AnalysisError.galaxyParameters")}</Title>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="galaxy-parameters"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
        />
      ) : (
        <GalaxyParameters galaxyJobErrors={galaxyJobErrors} />
      )}
    </Col>
  );
}
