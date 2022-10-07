/*
 * This file renders the analysis Share Results component
 */

/*
 * The following import statements makes available all the elements
 * required by the component
 */
import React, { useContext, useEffect, useState } from "react";
import { Button, Checkbox, List, Typography } from "antd";
import { AnalysisContext } from "../../../../contexts/AnalysisContext";
import { AnalysisDetailsContext } from "../../../../contexts/AnalysisDetailsContext";
import { AnalysisShareContext } from "../../../../contexts/AnalysisShareContext";

import { showNotification } from "../../../../modules/notifications";
import { SPACE_MD } from "../../../../styles/spacing";
import { InfoAlert } from "../../../../components/alerts/InfoAlert";
import { WarningAlert } from "../../../../components/alerts/WarningAlert";

import {
  getSharedProjects,
  updateSharedProject,
} from "../../../../apis/analysis/analysis";
import { TabPanelContent } from "../../../../components/tabs/TabPanelContent";

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
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const {
    analysisShareContext,
    storeSharedProjects,
    updateSharedProjectShareStatus,
  } = useContext(AnalysisShareContext);

  // Local state variable which stores the shared projects
  const [sharedProjects, setSharedProjects] = useState(
    analysisShareContext.sharedProjects.length > 0
      ? analysisShareContext.sharedProjects
      : null
  );

  function renderSharedProjectsList() {
    return (
      <List
        bordered
        dataSource={sharedProjects}
        renderItem={(item) => {
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
          <WarningAlert
            message={i18n("AnalysisShare.saveResultsPermanent")}
            style={{ marginBottom: SPACE_MD }}
          />
          <p>{i18n("AnalysisShare.saveResultsMessage")}</p>
          <Button
            type="primary"
            onClick={() => saveResultsToRelatedSamples()}
            id="save-results-btn"
          >
            {i18n("AnalysisShare.saveResultsToSamples")}
          </Button>
        </div>
      );
    } else {
      return (
        <InfoAlert
          message={i18n("AnalysisShare.resultsSaved")}
          style={{ marginBottom: SPACE_MD }}
        />
      );
    }
  }

  // Updates if analysis is shared with a project or not
  function onChange(e) {
    updateSharedProject({
      submissionId: analysisIdentifier,
      projectId: e.target.value,
      shareStatus: e.target.checked,
    }).then((res) => {
      showNotification({ text: res.message });
      updateSharedProjectShareStatus({
        projectId: e.target.value,
        shareStatus: e.target.checked,
      });
    });
  }

  // On load gets the projects which the analysis can be shared with
  useEffect(() => {
    if (analysisShareContext.sharedProjects != sharedProjects) {
      getSharedProjects(analysisIdentifier).then((data) => {
        //List of projects which results can be shared with (local state)
        setSharedProjects(data);
        storeSharedProjects({ sharedProjects: data });
      });
    }
  }, []);

  /* Renders the projects which an analysis can be shared with
   * and a save results to related samples if applicable
   */
  return (
    <TabPanelContent title={i18n("AnalysisShare.manageResults")}>
      {sharedProjects !== null ? (
        sharedProjects.length > 0 ? (
          <section style={{ marginTop: SPACE_MD }}>
            <Title level={4}>
              {i18n("AnalysisShare.shareResultsWithProjects")}
            </Title>
            {renderSharedProjectsList()}
          </section>
        ) : (
          <InfoAlert
            message={i18n("AnalysisShare.noProjectsToShareResultsWith")}
          />
        )
      ) : null}

      {analysisDetailsContext.canShareToSamples &&
      analysisDetailsContext.allowedToModifySample &&
      analysisContext.isCompleted ? (
        <section
          style={{ marginTop: SPACE_MD }}
          className="t-save-results-to-samples"
        >
          <Title level={4}>{i18n("AnalysisShare.saveResults")}</Title>
          {renderSaveToRelatedSamples()}
        </section>
      ) : null}
    </TabPanelContent>
  );
}
