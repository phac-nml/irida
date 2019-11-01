/*
 * This file renders the Sistr Information component
 */

import React from "react";
import { SuccessAlert } from "../../../../components/alerts/SuccessAlert";
import { ErrorAlert } from "../../../../components/alerts/ErrorAlert";
import { WarningAlert } from "../../../../components/alerts/WarningAlert";
import { BasicList } from "../../../../components/lists/BasicList";
import { Col, Typography } from "antd";
import { getI18N } from "../../../../utilities/i18n-utilties";

const { Title } = Typography;

export default function SistrInfo({ sistrResults, sampleName }) {
  function sistrInfo() {
    const qc_status = sistrResults.qc_status;

    return [
      {
        title: getI18N("AnalysisSistr.sampleName"),
        desc: sampleName
      },
      {
        title: getI18N("AnalysisSistr.qualityControlStatus"),
        desc:
          qc_status === "PASS" ? (
            <SuccessAlert message={qc_status} />
          ) : qc_status === "FAIL" ? (
            <ErrorAlert message={qc_status} />
          ) : (
            <WarningAlert message={qc_status} />
          )
      }
    ];
  }

  /*
   * Returns a simple list which displays labels and values
   * for the sistr information
   */
  return (
    <Col span={12}>
      <Title level={2} className="t-page-title">
        {getI18N("AnalysisSistr.sistrInformation")}
      </Title>
      <div>
        <BasicList dataSource={sistrInfo()} />
      </div>
    </Col>
  );
}
