/*
 * This file renders the error details for the analysis
 */

/*
 * The following import statements makes available all the elements
 * required by the component encompassed within
 */

import React, { useContext, useEffect, useState } from "react";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { Alert, Col, Spin, Tabs, Typography } from "antd";
import { getJobErrors } from "../../../apis/analysis/analysis";
import { getI18N } from "../../../utilities/i18n-utilties";
import styled from "styled-components";

import { PassTabs } from "./PassTabs";
import { GalaxyJobInfo } from "./GalaxyJobInfo";
import { GalaxyParameters } from "./GalaxyParameters";
import { StandardError } from "./StandardError";
import { StandardOutput } from "./StandardOutput";

const { Text, Title } = Typography;
const TabPane = Tabs.TabPane;

export default function AnalysisError(props) {
  const { analysisContext } = useContext(AnalysisContext);
  const [jobErrors, setJobErrors] = useState(null);
  const [ currActiveKey, setCurrActiveKey ] = useState(1);

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
    setCurrActiveKey(key.charAt(key.length-1));
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
              <Col span={12}>
                <Title level={3}>
                  {getI18N("AnalysisError.galaxyJobInfo")}
                </Title>
                {jobErrors.galaxyJobErrors.length > 1 ?
                        <PassTabs value={{tabName: "job-error-info", currActiveKey: currActiveKey, updateActiveKey: updateActiveKey, galaxyJobErrors: jobErrors.galaxyJobErrors, galaxyUrl: jobErrors.galaxyUrl}} />
                    :
                        <GalaxyJobInfo value={{galaxyJobErrors: jobErrors.galaxyJobErrors, galaxyUrl: jobErrors.galaxyUrl}} />
                    }
              </Col>
            </TabPane>

            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .parameters ? (
              <TabPane
                tab={getI18N("AnalysisError.galaxyParameters")}
                key="galaxy-parameters"
              >
                <Col span={12}>
                  <Title level={3}>{getI18N("AnalysisError.galaxyParameters")}</Title>
                  {jobErrors.galaxyJobErrors.length > 1 ?
                        <PassTabs value={{tabName: "galaxy-parameters", currActiveKey: currActiveKey, updateActiveKey: updateActiveKey, galaxyJobErrors: jobErrors.galaxyJobErrors}} />
                    :
                        <GalaxyParameters value={{galaxyJobErrors: jobErrors.galaxyJobErrors}} />
                  }
                </Col>
              </TabPane>
            ) : null}

            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .standardError ? (
              <TabPane
                tab={getI18N("AnalysisError.standardError")}
                key="standard-error"
              >
                <Col span={12}>
                  <Title level={3}>{getI18N("AnalysisError.standardError")}</Title>
                  {jobErrors.galaxyJobErrors.length > 1 ?
                        <PassTabs value={{tabName: "standard-error", currActiveKey: currActiveKey, updateActiveKey: updateActiveKey, galaxyJobErrors: jobErrors.galaxyJobErrors}} />
                    :
                        <StandardError value={{galaxyJobErrors: jobErrors.galaxyJobErrors}} />
                }
                </Col>
              </TabPane>
            ) : null}
            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .standardOutput ? (
              <TabPane
                tab={getI18N("AnalysisError.standardOutput")}
                key="standard-out"
              >
                <Col span={12}>
                  <Title level={3}>{getI18N("AnalysisError.standardOutput")}</Title>
                  {jobErrors.galaxyJobErrors.length > 1 ?
                        <PassTabs value={{tabName:"standard-out", currActiveKey: currActiveKey, updateActiveKey: updateActiveKey, galaxyJobErrors: jobErrors.galaxyJobErrors}} />
                    :
                        <StandardOutput value={{galaxyJobErrors: jobErrors.galaxyJobErrors}} />
                  }
                </Col>
              </TabPane>
            ) : null}
          </StyledTabs>
        ) : (
          <div style={{ display: "flex" }}>
            <Alert
              type="warning"
              showIcon
              message={
                <Text strong>
                  {getI18N("AnalysisError.noJobInfoAvailable")}
                </Text>
              }
            />
          </div>
        )
      ) : (
        <Spin />
      )}
    </>
  );
}
