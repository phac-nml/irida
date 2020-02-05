/*
 * This file renders the phylogenetic tree which includes
 * the output preview files
 */

import React, { Suspense } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";

import { SPACE_MD } from "../../../styles/spacing";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { grey1 } from "../../../styles/colors";
import { ANALYSIS, TREE } from "../routes";
import { setBaseUrl } from "../../../utilities/url-utilities";

const Tree = React.lazy(() => import("./tree/Tree"));
const OutputFilePreview = React.lazy(() =>
  import("./outputs/OutputFilePreview")
);

const { Content, Sider } = Layout;

export default function AnalysisPhylogeneticTree() {
  const DEFAULT_URL = setBaseUrl(ANALYSIS.TREE);
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
                selectedKeys={[keyname ? keyname[1] : TREE.PREVIEW]}
              >
                <Menu.Item key="preview">
                  <Link to={`${DEFAULT_URL}/${TREE.PREVIEW}`}>
                    {i18n("AnalysisPhylogeneticTree.tree")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="file_preview">
                  <Link to={`${DEFAULT_URL}/${TREE.FILE_PREVIEW}`}>
                    {i18n("AnalysisOutputs.outputFilePreview")}
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
              <Tree path={`${DEFAULT_URL}/${TREE.PREVIEW}`} default />
              <OutputFilePreview path={`${DEFAULT_URL}/${TREE.FILE_PREVIEW}`} />
            </Router>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
}
