import React from "react";
import { PassTabs } from "./PassTabs";
import { GalaxyJobInfo } from "./GalaxyJobInfo";
import { Col, Typography } from "antd";
import { getI18N } from "../../../utilities/i18n-utilties";

const { Title } = Typography;

export default function GalaxyJobInfoTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors,
  galaxyUrl
}) {
  return (
    <Col span={12}>
      <Title level={3}>{getI18N("AnalysisError.galaxyJobInfo")}</Title>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="job-error-info"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
          galaxyUrl={galaxyUrl}
        />
      ) : (
        <GalaxyJobInfo
          galaxyJobErrors={galaxyJobErrors}
          galaxyUrl={galaxyUrl}
        />
      )}
    </Col>
  );
}
