/*
 * This file renders the error details for the analysis
 */

/*
 * The following import statements makes available all the elements
 * required by the component encompassed within
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { Alert, Spin, Tabs } from "antd";
import { getJobErrors } from "../../../apis/analysis/analysis";
import { getI18N } from "../../../utilities/i18n-utilties";
import styled from "styled-components";

const GalaxyJobInfoTab = React.lazy(() => import("./GalaxyJobInfoTab"));
const GalaxyParametersTab = React.lazy(() => import("./GalaxyParametersTab"));
const StandardErrorTab = React.lazy(() => import("./StandardErrorTab"));
const StandardOutputTab = React.lazy(() => import("./StandardOutputTab"));

const TabPane = Tabs.TabPane;

export default function AnalysisError(props) {
  const { analysisContext } = useContext(AnalysisContext);
  const [jobErrors, setJobErrors] = useState(null);
  const [currActiveKey, setCurrActiveKey] = useState(1);

  const StyledTabs = styled(Tabs)`
    .ant-tabs-tab {
      @media only screen and (min-width: 800px) {
        width: 200px;
      }
    }
  `;

  // Sets the job errors into a local state variable on page load
  useEffect(() => {
    getJobErrors(analysisContext.analysis.identifier).then(data => {
      setJobErrors(data);
    });
  }, []);

  // Sets the current activekey for the 'Pass' tabs
  function updateActiveKey(key) {
    setCurrActiveKey(key.charAt(key.length - 1));
  }

  return (
    <>
      {jobErrors !== null ? (
        jobErrors.galaxyJobErrors !== null ? (
          <StyledTabs
            type="card"
            activeKey={
              props.defaultTabKey === "" || props.defaultTabKey === "job-error"
                ? "job-error-info"
                : props.defaultTabKey
            }
            onChange={props.updateNav}
            tabPosition="left"
            animated={false}
          >
            <TabPane
              tab={getI18N("AnalysisError.galaxyJobInfo")}
              key="job-error-info"
            >
              <GalaxyJobInfoTab
                currActiveKey={currActiveKey}
                updateActiveKey={updateActiveKey}
                galaxyJobErrors={jobErrors.galaxyJobErrors}
                galaxyUrl={jobErrors.galaxyUrl}
              />
            </TabPane>

            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .parameters ? (
              <TabPane
                tab={getI18N("AnalysisError.galaxyParameters")}
                key="galaxy-parameters"
              >
                <GalaxyParametersTab
                  currActiveKey={currActiveKey}
                  updateActiveKey={updateActiveKey}
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                />
              </TabPane>
            ) : null}

            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .standardError ? (
              <TabPane
                tab={getI18N("AnalysisError.standardError")}
                key="standard-error"
              >
                <StandardErrorTab
                  currActiveKey={currActiveKey}
                  updateActiveKey={updateActiveKey}
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                />
              </TabPane>
            ) : null}

            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .standardOutput ? (
              <TabPane
                tab={getI18N("AnalysisError.standardOutput")}
                key="standard-out"
              >
                <Suspense fallback={<Spin />}>
                  <StandardOutputTab
                    currActiveKey={currActiveKey}
                    updateActiveKey={updateActiveKey}
                    galaxyJobErrors={jobErrors.galaxyJobErrors}
                  />
                </Suspense>
              </TabPane>
            ) : null}
          </StyledTabs>
        ) : (
          <div style={{ display: "flex" }}>
            <Alert
              type="warning"
              showIcon
              message={getI18N("AnalysisError.noJobInfoAvailable")}
            />
          </div>
        )
      ) : (
        <Spin />
      )}
    </>
  );
}
