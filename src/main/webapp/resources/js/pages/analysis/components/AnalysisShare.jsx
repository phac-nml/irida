/*
 * This file renders the analysis Share Results component
 */

/*
 * The following import statements makes available all the elements
 * required by the component
 */
import React, { useContext, useEffect, useState } from "react";
import { Alert, Button, Checkbox, List, Typography } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../contexts/AnalysisDetailsContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { showNotification } from "../../../modules/notifications";
import { SPACE_MD } from "../../../styles/spacing";

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
    return (
      <List
        bordered
        dataSource={sharedProjects}
        renderItem={item => {
          return (
            <List.Item>
              <List.Item.Meta
                key={item.project.identifier}
                description={
                  <div>
                    <Checkbox.Group
                      defaultValue={
                        item.shared ? [item.project.identifier] : []
                      }
                    >
                      <Checkbox
                        value={item.project.identifier}
                        onChange={onChange}
                        defaultChecked={item.shared}
                      >
                        {item.project.name}
                      </Checkbox>
                    </Checkbox.Group>
                  </div>
                }
              />
            </List.Item>
          );
        }}
      />
    );
  }

  function renderSaveToRelatedSamples() {
    if (!analysisDetailsContext.updateSamples) {
      return (
        <div>
          <Alert
            type="warning"
            showIcon
            message={getI18N("AnalysisShare.saveResultsPermanent")}
            style={{ marginBottom: SPACE_MD }}
          />
          <p>{getI18N(`AnalysisShare.${analysisContext.analysisType.type}`)}</p>
          <Button
            type="primary"
            onClick={() => saveResultsToRelatedSamples()}
            id="save-results-btn"
          >
            {getI18N("AnalysisShare.saveResultsToSamples")}
          </Button>
        </div>
      );
    } else {
      return (
        <Alert
          type="info"
          showIcon
          message={getI18N("AnalysisShare.resultsSaved")}
          style={{ marginBottom: SPACE_MD }}
        />
      );
    }
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
      <Title level={2}>{getI18N("AnalysisShare.manageResults")}</Title>

      {sharedProjects !== null ? (
        sharedProjects.length > 0 ? (
          <section style={{ marginTop: SPACE_MD }}>
            <Title level={3}>
              {getI18N("AnalysisShare.shareResultsWithProjects")}
            </Title>
            {renderSharedProjectsList()}
          </section>
        ) : (
          <Alert
            type="info"
            showIcon
            message={getI18N("AnalysisShare.noProjectsToShareResultsWith")}
          />
        )
      ) : null}

      {analysisDetailsContext.canShareToSamples ? (
        <section style={{ marginTop: SPACE_MD }}>
          <Title level={3}>{getI18N("AnalysisShare.saveResults")}</Title>
          {renderSaveToRelatedSamples()}
        </section>
      ) : null}
    </>
  );
}
