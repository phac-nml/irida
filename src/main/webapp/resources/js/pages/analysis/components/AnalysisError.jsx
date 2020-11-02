/*
 * This file renders the error details for the analysis
 */

/*
 * The following import statements makes available all the elements
 * required by the component encompassed within
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { Button, Descriptions, Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { AnalysisContext, isAdmin } from "../../../contexts/AnalysisContext";
import { getJobErrors } from "../../../apis/analysis/analysis";

import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { SPACE_MD } from "../../../styles/spacing";
import { ANALYSIS, ERROR } from "../routes";
import { grey1 } from "../../../styles/colors";

import { setBaseUrl } from "../../../utilities/url-utilities";
import { Monospace } from "../../../components/typography";

const GalaxyJobInfoTab = React.lazy(() =>
  import("./jobErrors/GalaxyJobInfoTab")
);
const GalaxyParametersTab = React.lazy(() =>
  import("./jobErrors/GalaxyParametersTab")
);
const StandardErrorTab = React.lazy(() =>
  import("./jobErrors/StandardErrorTab")
);
const StandardOutputTab = React.lazy(() =>
  import("./jobErrors/StandardOutputTab")
);

const { Content, Sider } = Layout;

export default function AnalysisError() {
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const [jobErrors, setJobErrors] = useState(null);
  const [currActiveKey, setCurrActiveKey] = useState(1);

  const DEFAULT_URL =
    setBaseUrl(`/analysis/${analysisIdentifier}/` + ANALYSIS.ERROR);
  const pathRegx = new RegExp(/([a-zA-Z\-]+)$/);

  // Sets the job errors into a local state variable on page load
  useEffect(() => {
    getJobErrors(analysisIdentifier).then(data => {
      setJobErrors(data);
    });
  }, []);

  // Sets the current active key for the 'Pass' tabs
  function updateActiveKey(key) {
    setCurrActiveKey(key.charAt(key.length - 1));
  }

  return jobErrors !== null ? (
    jobErrors.galaxyJobErrors !== null ? (
      <Layout>
        <Sider width={200} style={{ backgroundColor: grey1 }}>
          <Location>
            {props => {
              const keyname = props.location.pathname.match(pathRegx);
              return (
                <Menu
                  mode="vertical"
                  selectedKeys={[keyname ? keyname[1] : ERROR.JOB_ERROR_INFO]}
                >
                  <Menu.Item key="job-error-info">
                    <Link to={`${DEFAULT_URL}/${ERROR.JOB_ERROR_INFO}`}>
                      {i18n("AnalysisError.galaxyJobInfo")}
                    </Link>
                  </Menu.Item>
                  {jobErrors.galaxyJobErrors[
                    jobErrors.galaxyJobErrors.length - 1
                  ].parameters ? (
                    <Menu.Item key="galaxy-parameters">
                      <Link to={`${DEFAULT_URL}/${ERROR.GALAXY_PARAMETERS}`}>
                        {i18n("AnalysisError.galaxyParameters")}
                      </Link>
                    </Menu.Item>
                  ) : null}
                  {jobErrors.galaxyJobErrors[
                    jobErrors.galaxyJobErrors.length - 1
                  ].standardError ? (
                    <Menu.Item key="standard-error">
                      <Link to={`${DEFAULT_URL}/${ERROR.STANDARD_ERROR}`}>
                        {i18n("AnalysisError.standardError")}
                      </Link>
                    </Menu.Item>
                  ) : null}
                  {jobErrors.galaxyJobErrors[
                    jobErrors.galaxyJobErrors.length - 1
                  ].standardOutput ? (
                    <Menu.Item key="standard-out">
                      <Link to={`${DEFAULT_URL}/${ERROR.STANDARD_OUT}`}>
                        {i18n("AnalysisError.standardOutput")}
                      </Link>
                    </Menu.Item>
                  ) : null}
                </Menu>
              );
            }}
          </Location>
        </Sider>

        <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
          <Content>
            <Suspense fallback={<ContentLoading />}>
              <Router>
                <GalaxyJobInfoTab
                  currActiveKey={currActiveKey}
                  updateActiveKey={updateActiveKey}
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                  galaxyUrl={jobErrors.galaxyUrl}
                  path={`${DEFAULT_URL}/${ERROR.JOB_ERROR_INFO}`}
                  default
                />
                <GalaxyParametersTab
                  currActiveKey={currActiveKey}
                  updateActiveKey={updateActiveKey}
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                  path={`${DEFAULT_URL}/${ERROR.GALAXY_PARAMETERS}`}
                />
                <StandardErrorTab
                  currActiveKey={currActiveKey}
                  updateActiveKey={updateActiveKey}
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                  path={`${DEFAULT_URL}/${ERROR.STANDARD_ERROR}`}
                />
                <StandardOutputTab
                  currActiveKey={currActiveKey}
                  updateActiveKey={updateActiveKey}
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                  path={`${DEFAULT_URL}/${ERROR.STANDARD_OUT}`}
                />
              </Router>
            </Suspense>
          </Content>
        </Layout>
      </Layout>
    ) : (
      <div>
        <div style={{ display: "flex" }}>
          <WarningAlert message={i18n("AnalysisError.noJobInfoAvailable")} />
        </div>
        { analysisContext.isAdmin && jobErrors.galaxyHistoryId !== null ?
          <div style={{ display: "flex", marginTop: SPACE_MD }} id="t-galaxy-history-id">
            <Descriptions title={i18n("AnalysisError.galaxyInformation")} column={1} bordered={true}>
              <Descriptions.Item label={i18n("AnalysisError.historyId")}>
                <Button
                  type="link"
                  style={{ paddingLeft: 0 }}
                  href={`${jobErrors.galaxyUrl}/histories/view?id=${jobErrors.galaxyHistoryId}`}
                  target="_blank"
                >
                  <Monospace>{jobErrors.galaxyHistoryId}</Monospace>
                </Button>
              </Descriptions.Item>
            </Descriptions>
        </div>
          : null
        }
      </div>
    )
  ) : (
    <ContentLoading />
  );
}
