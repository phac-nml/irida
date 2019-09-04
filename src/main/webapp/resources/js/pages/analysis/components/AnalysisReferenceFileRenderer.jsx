import React, { useContext } from "react";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { SPACE_LG, SPACE_XS } from "../../../styles/spacing";
import { FONT_WEIGHT_DEFAULT } from "../../../styles/fonts";
import { Row, Col, Icon, Button } from "antd";

export function AnalysisReferenceFileRenderer() {
  const { analysisDetailsContext } = useContext(
    AnalysisDetailsContext
  );

    const renderReferenceFile = () => {
      const referenceFile = [];

      if (analysisDetailsContext.referenceFile.length === 0) {
        return null;
      } else {
        referenceFile.push(
          <div style={{ marginBottom: SPACE_LG }} key="samplesDiv-1">
            <h4
              style={{ fontWeight: FONT_WEIGHT_DEFAULT }}
              key="reference-file-heading-1"
            >
              {getI18N("analysis.tab.content.samples.reference-file")}
            </h4>
            <Row key="row-reference-file-1">
              <span key="reference-file-1">
                {analysisDetailsContext.referenceFile.label}
              </span>
              <Button
                key="reference-file-1-download-button"
                className="pull-right"
                style={{ marginTop: SPACE_XS }}
                style={{ marginTop: SPACE_XS }}
                type="primary"
                onClick={() => {
                  downloadReferenceFile();
                }}
              >
                <Icon type="download" />{" "}
                {getI18N("analysis.tab.content.samples.download-reference-file")}
              </Button>
            </Row>
          </div>
        );
        return referenceFile;
      }
    };

    const downloadReferenceFile = () => {
        if (analysisDetailsContext.referenceFile.identifier !== undefined) {
          window.open(
            `${window.TL.BASE_URL}referenceFiles/download/${analysisDetailsContext.referenceFile.identifier}`,
            "_blank"
          );
        }
    };

  return (
    <>
        {renderReferenceFile()}
    </>
  );
}
