/**
 * @File component renders the bio hansel results.
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { SPACE_MD } from "../../../styles/spacing";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import {
  getDataViaChunks,
  getOutputInfo
} from "../../../apis/analysis/analysis";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { getI18N } from "../../../utilities/i18n-utilities";
import { WarningAlert } from "../../../components/alerts/WarningAlert";

const BioHanselInfo = React.lazy(() => import("./biohansel/BioHanselInfo"));
const OutputFilePreviewContainer = React.lazy(() =>
  import("./OutputFilePreviewContainer")
);

const { Content, Sider } = Layout;

export default function AnalysisBioHansel() {
  const { analysisContext } = useContext(AnalysisContext);
  const [bioHanselResults, setBioHanselResults] = useState(null);

  const BASE_URL = `${window.PAGE.base}/biohansel`;
  const pathRegx = new RegExp(/([a-zA-Z_]+)$/);

  // On load gets the bio hansel results. If the file is not found then set to undefined
  useEffect(() => {
    getOutputInfo(analysisContext.analysis.identifier).then(data => {
      const outputInfo = data.find(output => {
        return output.filename === "bio_hansel-results.json";
      });

      if (outputInfo !== undefined) {
        getDataViaChunks({
          submissionId: analysisContext.analysis.identifier,
          fileId: outputInfo.id,
          seek: 0,
          chunk: outputInfo.fileSizeBytes
        }).then(data => {
          const { text } = data;
          const parsedResults = JSON.parse(text);
          setBioHanselResults(parsedResults[0]);
        });
      } else {
        setBioHanselResults(undefined);
      }
    });
  }, []);

  return (
    <Layout>
      <Sider width={200} style={{ background: "#fff" }}>
        <Location>
          {props => {
            const keyname = props.location.pathname.match(pathRegx);
            return (
              <Menu
                mode="vertical"
                selectedKeys={[keyname ? keyname[1] : "info"]}
              >
                <Menu.Item key="info">
                  <Link to={`${BASE_URL}/info`}>
                    {getI18N("AnalysisBioHansel.bioHanselInformation")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="file_preview">
                  <Link to={`${BASE_URL}/file_preview`}>
                    {getI18N("AnalysisOutputs.outputFilePreview")}
                  </Link>
                </Menu.Item>
              </Menu>
            );
          }}
        </Location>
      </Sider>

      <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: "white" }}>
        <Content>
          {bioHanselResults === undefined ? (
            <WarningAlert
              message={getI18N("AnalysisBioHansel.resultsUnavailable")}
            />
          ) : bioHanselResults !== null ? (
            <Suspense fallback={<ContentLoading />}>
              <Router>
                <BioHanselInfo
                  bioHanselResults={bioHanselResults}
                  path={`${BASE_URL}/info`}
                  default
                />
                <OutputFilePreviewContainer path={`${BASE_URL}/file_preview`} />
              </Router>
            </Suspense>
          ) : (
            <ContentLoading />
          )}
        </Content>
      </Layout>
    </Layout>
  );
}
