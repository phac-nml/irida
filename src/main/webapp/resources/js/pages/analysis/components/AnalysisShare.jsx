import React, { useContext, useEffect, useState } from "react";
import { Button, Checkbox, Card, Row, Alert } from "antd";
import { AnalysisContext } from "../../../state/AnalysisState";
import { getI18N } from "../../../utilities/i18n-utilties";
import { showNotification } from "../../../modules/notifications";

import {
  getSharedProjects,
  updateSharedProjects,
  saveToRelatedSamples
} from "../../../apis/analysis/analysis";

export default function AnalysisShare() {
  const { context, dispatch } = useContext(AnalysisContext);
  const [sharedProjects, setSharedProjects] = useState(null);

  function renderSharedProjectsList() {
    const projectList = sharedProjects.map((sharedProject, index) =>
      <Row className="spaced-bottom" key={`sharedprojrow${index}`}>
         <Checkbox
           key={`sharedproj${index}`}
           value={sharedProject.project.identifier}
           onChange={onChange}
         >
           {sharedProject.project.name}
         </Checkbox>
       </Row>
    );
    return projectList;
  }

  function onChange(e) {
    updateSharedProjects(
      context.analysis.identifier,
      e.target.value,
      e.target.checked
    ).then(res => showNotification({ text: res.message }));
  }

  function handleSaveResults() {
    saveToRelatedSamples(context.analysis.identifier).then(res =>
        showNotification({ text: res.message })
    );
    dispatch({ type: 'UPDATE_SAMPLES', updateSamples: true });
  }

  useEffect(() => {
    getSharedProjects(context.analysis.identifier).then(res =>
      //List of projects which results can be shared with
      setSharedProjects(res.data)
    );
  }, []);

  return (
    <>
      <h2 style={{ fontWeight: "bold" }}>
        {getI18N("analysis.tab.content.share.results.results")}
      </h2>

      <br />
      { sharedProjects != null ?
          <Card title="Share Results with Projects">
            { sharedProjects.length > 0 ?
                renderSharedProjectsList()
                :
                <p>{getI18N("analysis.tab.content.share.no-projects-to-share-results-with")}</p>
            }
          </Card>
          :null
      }

      <br />
      <br />

      {context.canShareToSamples == true ? (
        <Card title={getI18N("analysis.details.save.samples.title")}>
          {context.updateSamples == true ?
            (
                <Alert
                  message={getI18N("analysis.details.save.complete")}
                  type="info"
                />
            ) :
            <p className="spaced_bottom">
              {getI18N(
                `workflow.label.share-analysis-samples.${context.analysisType.type}`
              )}
            </p>
          }

          <Button
            type="primary"
            className="spaced-top"
            disabled={context.updateSamples ? true : false}
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
