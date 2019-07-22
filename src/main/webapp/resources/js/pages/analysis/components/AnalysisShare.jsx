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
  const { state, dispatch } = useContext(AnalysisContext);
  const [sharedProjects, setSharedProjects] = useState(null);

  function renderSharedProjectsList() {
    const projectList = [];

    for (let i = 0; i < sharedProjects.length; i++) {
      projectList.push(
        <Row className="spaced-bottom" key={`sharedprojrow${i}`}>
          <Checkbox
            key={`sharedproj${i}`}
            value={sharedProjects[i].project.identifier}
            onChange={onChange}
          >
            {sharedProjects[i].project.name}
          </Checkbox>
        </Row>
      );
    }
    return projectList;
  }

  function onChange(e) {
    updateSharedProjects(
      state.analysis.identifier,
      e.target.value,
      e.target.checked
    ).then(res => showNotification({ text: res.message }));
  }

  function handleSaveResults() {
    saveToRelatedSamples(state.analysis.identifier).then(res =>
        showNotification({ text: res.message })
    );
    dispatch({ type: 'UPDATE_SAMPLES', updateSamples: true });
  }

  useEffect(() => {
    getSharedProjects(state.analysis.identifier).then(res =>
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
      <Card title="Share Results with Projects">
        { sharedProjects !== null ?
            renderSharedProjectsList()
            :
            <p>{getI18N("analysis.tab.content.share.no-projects-to-share-results-with")}</p>
        }
      </Card>

      <br />
      <br />

      {state.canShareToSamples ? (
        <Card title={getI18N("analysis.details.save.samples.title")}>
          {state.updateSamples ?
            (
                <Alert
                  message={getI18N("analysis.details.save.complete")}
                  type="info"
                />
            ) :
            <p className="spaced_bottom">
              {getI18N(
                `workflow.label.share-analysis-samples.${state.analysisType.type}`
              )}
            </p>
          }

          <Button
            type="primary"
            className="spaced-top"
            disabled={state.updateSamples ? true : false}
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
