/*
 * The following import statements makes available all the elements
 * required by the component
 */

import React, { useContext } from "react";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisSamplesContext } from "../../../contexts/AnalysisSamplesContext";
import { SPACE_LG } from "../../../styles/spacing";
import { FONT_WEIGHT_DEFAULT } from "../../../styles/fonts";
import { Row, Col, Icon, Button } from "antd";

export function AnalysisReferenceFileRenderer() {
  /*
   * The following const statement
   * make the required context which contains
   * the state and methods available to the component
   */
  const { analysisSamplesContext } = useContext(AnalysisSamplesContext);

  const renderReferenceFile = () => {
    const referenceFile = [];

    if (analysisSamplesContext.referenceFile.length === 0) {
      return null;
    } else {
      referenceFile.push(
        <div style={{ marginBottom: SPACE_LG }} key="samplesDiv-1">
          <h4
            style={{ fontWeight: FONT_WEIGHT_DEFAULT }}
            key="reference-file-heading-1"
          >
            {getI18N("AnalysisSamples.referenceFile")}
          </h4>
          <Row key="row-reference-file-1">
            <span
              key="reference-file-1"
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center"
              }}
            >
              {analysisSamplesContext.referenceFile.label}

              <Button
                key="reference-file-1-download-button"
                type="default"
                onClick={() => {
                  downloadReferenceFile();
                }}
              >
                <Icon type="download" />{" "}
                {getI18N("AnalysisSamples.downloadReferenceFile")}
              </Button>
            </span>
          </Row>
        </div>
      );
      return referenceFile;
    }
  };

  const downloadReferenceFile = () => {
    if (analysisSamplesContext.referenceFile.identifier !== undefined) {
      window.open(
        `${window.TL.BASE_URL}referenceFiles/download/${analysisSamplesContext.referenceFile.identifier}`,
        "_blank"
      );
    }
  };

  return <>{renderReferenceFile()}</>;
}
