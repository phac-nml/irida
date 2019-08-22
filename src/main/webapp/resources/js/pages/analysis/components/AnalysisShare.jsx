import React, { useContext, useEffect, useState } from "react";
import { Button, Checkbox, Card, Row, Alert } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from '../../../contexts/AnalysisDetailsContext';
import { getI18N } from "../../../utilities/i18n-utilties";
import { showNotification } from "../../../modules/notifications";

import {
  getSharedProjects,
  updateSharedProjects,
  saveToRelatedSamples
} from "../../../apis/analysis/analysis";

export default function AnalysisShare() {
  const { analysisDetailsContext, saveResultsToRelatedSamples } = useContext(AnalysisDetailsContext);
  const { analysisContext } = useContext(AnalysisContext);
  const [sharedProjects, setSharedProjects] = useState(null);

  function renderSharedProjectsList() {
    const projectList = sharedProjects.map((sharedProject, index) =>
      <Row className="spaced-bottom" key={`sharedprojrow${index}`}>
         <Checkbox
           key={`sharedproj${index}`}
           value={sharedProject.project.identifier}
           onChange={onChange}
           defaultChecked={sharedProject.shared ? true : false}
         >
           {sharedProject.project.name}
         </Checkbox>
       </Row>
    );
    return projectList;
  }

  function onChange(e) {
    // Updates if analysis is shared with a project or not
    updateSharedProjects(
      analysisContext.analysis.identifier,
      e.target.value,
      e.target.checked
    ).then(res => showNotification({ text: res.message }));
  }

  function handleSaveResults() {
    saveResultsToRelatedSamples();
  }

  useEffect(() => {
    getSharedProjects(analysisContext.analysis.identifier).then(data =>
      //List of projects which results can be shared with (local state)
      setSharedProjects(data)
    );
  }, []);

  return (
    <>
      <h2 style={{ fontWeight: "bold" }}>
        {getI18N("analysis.tab.content.share.results.results")}
      </h2>

      { sharedProjects != null ?
          <Card title="Share Results with Projects" style={{marginTop:"10px"}}>
            { sharedProjects.length > 0 ?
                renderSharedProjectsList()
                :
                <p>{getI18N("analysis.tab.content.share.no-projects-to-share-results-with")}</p>
            }
          </Card>
          :null
      }

      {analysisDetailsContext.canShareToSamples == true ? (
        <Card title={getI18N("analysis.details.save.samples.title")} style={{marginTop:"10px"}}>
          {analysisDetailsContext.updateSamples == true ?
            (
                <Alert
                  message={getI18N("analysis.details.save.complete")}
                  type="info"
                />
            ) :
            <p className="spaced_bottom">
              {getI18N(
                `workflow.label.share-analysis-samples.${analysisContext.analysisType.type}`
              )}
            </p>
          }

          <Button
            type="primary"
            className="spaced-top"
            disabled={analysisDetailsContext.updateSamples == true ? true : false}
            onClick={handleSaveResults}
            id="save-results-btn"
          >
            {getI18N("analysis.details.save.samples.button")}
          </Button>
        </Card>
      ) : null}
    </>
  );
}
