/*
 * This file renders the analysis Share Results component
 */

/*
 * The following import statements makes available all the elements
 * required by the component
 */
import React, { useContext, useEffect, useState } from "react";
import { Button, Checkbox, Card, Row, Alert, Typography } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { showNotification } from "../../../modules/notifications";
import { SPACE_SM } from "../../../styles/spacing";

import {
  getSharedProjects,
  updateSharedProjects
} from "../../../apis/analysis/analysis";

const { Title } = Typography;

export default function AnalysisShare() {
  /*
   * The following const statements
   * make the required contexts which contain
   * the state and methods available to the component
   */
  const { analysisDetailsContext, saveResultsToRelatedSamples } = useContext(
    AnalysisDetailsContext
  );
  const { analysisContext } = useContext(AnalysisContext);

  // Local state variable which stores the shared projects
  const [sharedProjects, setSharedProjects] = useState(null);

  function renderSharedProjectsList() {
    return sharedProjects.map((sharedProject, index) => (
      <Row className="spaced-bottom" key={`sharedprojrow${index}`}>
        <Checkbox
          key={`sharedproj${index}`}
          value={sharedProject.project.identifier}
          onChange={onChange}
          defaultChecked={sharedProject.shared}
        >
          {sharedProject.project.name}
        </Checkbox>
      </Row>
    ));
  }

  // Updates if analysis is shared with a project or not
  function onChange(e) {
    updateSharedProjects({
      submissionId: analysisContext.analysis.identifier,
      projectId: e.target.value,
      shareStatus: e.target.checked
    }).then(res => showNotification({ text: res.message }));
  }

  // On load gets the projects which the analysis can be shared with
  useEffect(() => {
    getSharedProjects(analysisContext.analysis.identifier).then(data =>
      //List of projects which results can be shared with (local state)
      setSharedProjects(data)
    );
  }, [setSharedProjects]);

  /* Renders the projects which an analysis can be shared with
   * and a save results to related samples if applicable
   */
  return (
    <>
      <Title level={2}>{getI18N("AnalysisShare.results")}</Title>

      {sharedProjects !== null ? (
        <Card
          title={getI18N("AnalysisShare.shareResultsWithProjects")}
          style={{ marginTop: SPACE_SM }}
        >
          {sharedProjects.length > 0 ? (
            renderSharedProjectsList()
          ) : (
            <p>{getI18N("AnalysisShare.noProjectsToShareResultsWith")}</p>
          )}
        </Card>
      ) : null}

      {analysisDetailsContext.canShareToSamples ? (
        <Card
          title={getI18N("AnalysisShare.saveResults")}
          style={{ marginTop: SPACE_SM }}
        >
          {analysisDetailsContext.updateSamples ? (
            <Alert
              message={getI18N("AnalysisShare.resultsSaved")}
              type="info"
            />
          ) : (
            <p className="spaced_bottom">
              {getI18N(`AnalysisShare.${analysisContext.analysisType.type}`)}
            </p>
          )}

          <Button
            type="primary"
            className="spaced-top"
            disabled={analysisDetailsContext.updateSamples}
            onClick={() => saveResultsToRelatedSamples()}
            id="save-results-btn"
          >
            {getI18N("AnalysisShare.saveResultsToSamples")}
          </Button>
        </Card>
      ) : null}
    </>
  );
}
