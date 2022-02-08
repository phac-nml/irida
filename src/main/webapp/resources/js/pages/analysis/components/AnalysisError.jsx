/*
 * This file renders the error details for the analysis
 */

/*
 * The following import statements makes available all the elements
 * required by the component encompassed within
 */

import { Button, Descriptions, Layout, Menu } from "antd";
import React, { Suspense, useContext } from "react";
import { Link, Route, Routes, useLocation } from "react-router-dom";
import { getJobErrors } from "../../../apis/analysis/analysis";

import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { Monospace } from "../../../components/typography";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { grey1 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";

import { setBaseUrl } from "../../../utilities/url-utilities";
import { ANALYSIS, ERROR } from "../routes";

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
  const location = useLocation();
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);
  const [jobErrors, setJobErrors] = React.useState(null);
  const [currActiveKey, setCurrActiveKey] = React.useState(1);
  const [keyName, setKeyName] = React.useState("");

  const DEFAULT_URL = setBaseUrl(
    `/analysis/${analysisIdentifier}/` + ANALYSIS.ERROR
  );

  const pathRegex = /\/error\/(?<path>([\w-]+))/;

  // Sets the job errors into a local state variable on page load
  React.useEffect(() => {
    getJobErrors(analysisIdentifier).then((data) => {
      setJobErrors(data);
    });
  }, []);

  // Set the current active key for the error submenu
  React.useEffect(() => {
    const found = location.pathname.match(pathRegex);
    if (found) {
      setKeyName(found.groups.path);
    } else {
      setKeyName("job-error-info");
    }
  }, [location.pathname]);

  // Sets the current active key for the 'Pass' tabs
  function updateActiveKey(key) {
    setCurrActiveKey(key.charAt(key.length - 1));
  }

  return jobErrors !== null ? (
    jobErrors.galaxyJobErrors !== null ? (
      <Layout>
        <Sider width={200} style={{ backgroundColor: grey1 }}>
          <Menu mode="vertical" selectedKeys={[keyName]}>
            <Menu.Item key="job-error-info">
              <Link to={`${DEFAULT_URL}/${ERROR.JOB_ERROR_INFO}`}>
                {i18n("AnalysisError.galaxyJobInfo")}
              </Link>
            </Menu.Item>
            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .parameters ? (
              <Menu.Item key="galaxy-parameters">
                <Link to={`${DEFAULT_URL}/${ERROR.GALAXY_PARAMETERS}`}>
                  {i18n("AnalysisError.galaxyParameters")}
                </Link>
              </Menu.Item>
            ) : null}
            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .standardError ? (
              <Menu.Item key="standard-error">
                <Link to={`${DEFAULT_URL}/${ERROR.STANDARD_ERROR}`}>
                  {i18n("AnalysisError.standardError")}
                </Link>
              </Menu.Item>
            ) : null}
            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .standardOutput ? (
              <Menu.Item key="standard-out">
                <Link to={`${DEFAULT_URL}/${ERROR.STANDARD_OUT}`}>
                  {i18n("AnalysisError.standardOutput")}
                </Link>
              </Menu.Item>
            ) : null}
          </Menu>
        </Sider>

        <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
          <Content>
            <Suspense fallback={<ContentLoading />}>
              <Routes>
                <Route
                  path={ERROR.JOB_ERROR_INFO}
                  element={
                    <GalaxyJobInfoTab
                      currActiveKey={currActiveKey}
                      updateActiveKey={updateActiveKey}
                      galaxyJobErrors={jobErrors.galaxyJobErrors}
                      galaxyUrl={jobErrors.galaxyUrl}
                    />
                  }
                />
                <Route
                  path={ERROR.GALAXY_PARAMETERS}
                  element={
                    <GalaxyParametersTab
                      currActiveKey={currActiveKey}
                      updateActiveKey={updateActiveKey}
                      galaxyJobErrors={jobErrors.galaxyJobErrors}
                    />
                  }
                />
                <Route
                  path={ERROR.STANDARD_ERROR}
                  element={
                    <StandardErrorTab
                      currActiveKey={currActiveKey}
                      updateActiveKey={updateActiveKey}
                      galaxyJobErrors={jobErrors.galaxyJobErrors}
                    />
                  }
                />
                <Route
                  path={ERROR.STANDARD_OUT}
                  element={
                    <StandardOutputTab
                      currActiveKey={currActiveKey}
                      updateActiveKey={updateActiveKey}
                      galaxyJobErrors={jobErrors.galaxyJobErrors}
                    />
                  }
                />
                <Route
                  path={"*"}
                  element={
                    <GalaxyJobInfoTab
                      currActiveKey={currActiveKey}
                      updateActiveKey={updateActiveKey}
                      galaxyJobErrors={jobErrors.galaxyJobErrors}
                      galaxyUrl={jobErrors.galaxyUrl}
                    />
                  }
                />
              </Routes>
            </Suspense>
          </Content>
        </Layout>
      </Layout>
    ) : (
      <div>
        <div style={{ display: "flex" }}>
          <WarningAlert message={i18n("AnalysisError.noJobInfoAvailable")} />
        </div>
        {analysisContext.isAdmin && jobErrors.galaxyHistoryId !== null ? (
          <div
            style={{ display: "flex", marginTop: SPACE_MD }}
            id="t-galaxy-history-id"
          >
            <Descriptions
              title={i18n("AnalysisError.galaxyInformation")}
              column={1}
              bordered={true}
            >
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
        ) : null}
      </div>
    )
  ) : (
    <ContentLoading />
  );
}
