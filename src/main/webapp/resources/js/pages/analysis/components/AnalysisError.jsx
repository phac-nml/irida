/*
 * This file renders the error details for the analysis
 */

/*
 * The following import statements makes available all the elements
 * required by the component encompassed within
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getJobErrors } from "../../../apis/analysis/analysis";
import { getI18N } from "../../../utilities/i18n-utilties";
import { WarningAlert } from "../../../components/alerts/WarningAlert";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { SPACE_MD } from "../../../styles/spacing";

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

export default function AnalysisError(props) {
  const { analysisContext } = useContext(AnalysisContext);
  const [jobErrors, setJobErrors] = useState(null);

  const pathRegx = new RegExp(/([a-zA-Z\-]+)$/);

  // Sets the job errors into a local state variable on page load
  useEffect(() => {
    getJobErrors(analysisContext.analysis.identifier).then(data => {
      setJobErrors(data);
    });
  }, []);

  return jobErrors !== null ? (
    jobErrors.galaxyJobErrors !== null ? (
      <Layout>
        <Sider width={200} style={{ background: "#fff" }}>
          <Location>
            {props => {
              const keyname = props.location.pathname.match(pathRegx);
              return (
                <Menu
                  mode="vertical"
                  selectedKeys={[keyname ? keyname[1] : "job-error-info"]}
                >
                  <Menu.Item key="job-error-info">
                    <Link to="job-error-info">
                      {getI18N("AnalysisError.galaxyJobInfo")}
                    </Link>
                  </Menu.Item>
                  {jobErrors.galaxyJobErrors[
                    jobErrors.galaxyJobErrors.length - 1
                  ].parameters ? (
                    <Menu.Item key="galaxy-parameters">
                      <Link to="galaxy-parameters">
                        {getI18N("AnalysisError.galaxyParameters")}
                      </Link>
                    </Menu.Item>
                  ) : null}
                  {jobErrors.galaxyJobErrors[
                    jobErrors.galaxyJobErrors.length - 1
                  ].standardError ? (
                    <Menu.Item key="standard-error">
                      <Link to="standard-error">
                        {getI18N("AnalysisError.standardError")}
                      </Link>
                    </Menu.Item>
                  ) : null}
                  {jobErrors.galaxyJobErrors[
                    jobErrors.galaxyJobErrors.length - 1
                  ].standardOutput ? (
                    <Menu.Item key="standard-out">
                      <Link to="standard-out">
                        {getI18N("AnalysisError.standardOutput")}
                      </Link>
                    </Menu.Item>
                  ) : null}
                </Menu>
              );
            }}
          </Location>
        </Sider>

        <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: "white" }}>
          <Content>
            <Suspense fallback={<ContentLoading />}>
              <Router>
                <GalaxyJobInfoTab
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                  galaxyUrl={jobErrors.galaxyUrl}
                  path="job-error-info"
                  default
                />
                <GalaxyParametersTab
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                  path="galaxy-parameters"
                />
                <StandardErrorTab
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                  path="standard-error"
                />
                <StandardOutputTab
                  galaxyJobErrors={jobErrors.galaxyJobErrors}
                  path="standard-error"
                />
              </Router>
            </Suspense>
          </Content>
        </Layout>
      </Layout>
    ) : (
      <div style={{ display: "flex" }}>
        <WarningAlert message={getI18N("AnalysisError.noJobInfoAvailable")} />
      </div>
    )
  ) : (
    <ContentLoading />
  );
}
