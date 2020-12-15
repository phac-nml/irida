/*
 * The following import statements makes available all the elements
 * required by the component
 */

import React, { useContext } from "react";

import { AnalysisSamplesContext } from "../../../../contexts/AnalysisSamplesContext";
import { SPACE_LG, SPACE_XS } from "../../../../styles/spacing";
import { Button, Typography } from "antd";
import { IconDownloadFile } from "../../../../components/icons/Icons";
import { downloadProjectReferenceFile } from "../../../../apis/references/reference-files";

const { Title } = Typography;

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
          <Title level={4}>{i18n("AnalysisSamples.referenceFile")}</Title>
          <div
            key="row-reference-file-1"
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            {analysisSamplesContext.referenceFile.label}

            <Button
              key="reference-file-1-download-button"
              type="default"
              onClick={() => {
                downloadReferenceFile();
              }}
              className="t-reference-file-download-btn"
            >
              <IconDownloadFile style={{ marginRight: SPACE_XS }} />
              {i18n("AnalysisSamples.downloadReferenceFile")}
            </Button>
          </div>
        </div>
      );
      return referenceFile;
    }
  };

  const downloadReferenceFile = () => {
    if (analysisSamplesContext.referenceFile.identifier !== undefined) {
      downloadProjectReferenceFile(
        analysisSamplesContext.referenceFile.identifier
      );
    }
  };

  return <>{renderReferenceFile()}</>;
}
