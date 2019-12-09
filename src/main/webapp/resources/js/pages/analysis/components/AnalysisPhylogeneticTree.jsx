/*
 * This file renders the phylogenetic tree which includes
 * the output preview files
 */

import React, { Suspense } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { getI18N } from "../../../utilities/i18n-utilities";
import { SPACE_MD } from "../../../styles/spacing";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { grey1 } from "../../../styles/colors";

const Tree = React.lazy(() => import("./tree/Tree"));
const OutputFilePreview = React.lazy(() =>
  import("./outputs/OutputFilePreview")
);

const { Content, Sider } = Layout;

export default function AnalysisPhylogeneticTree(props) {
  const BASE_URL = `${window.PAGE.base}/tree`;
  const pathRegx = new RegExp(/([a-zA-Z_]+)$/);

  /*
   * The following renders the components for the Phylogenetic Tree tabs
   */
  return (
    <Layout>
      <Sider width={200} style={{ backgroundColor: grey1 }}>
        <Location>
          {props => {
            const keyname = props.location.pathname.match(pathRegx);
            return (
              <Menu
                mode="vertical"
                selectedKeys={[keyname ? keyname[1] : "preview"]}
              >
                <Menu.Item key="preview">
                  <Link to={`${BASE_URL}/preview`}>
                    {getI18N("AnalysisPhylogeneticTree.tree")}
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

      <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
        <Content>
          <Suspense fallback={<ContentLoading />}>
            <Router>
              <Tree path={`${BASE_URL}/preview`} default />
              <OutputFilePreview path={`${BASE_URL}/file_preview`} />
            </Router>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
}
