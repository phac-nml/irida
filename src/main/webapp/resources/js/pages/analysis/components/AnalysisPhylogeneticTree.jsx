/*
 * This file renders the phylogenetic tree which includes
 * the output preview files
 */

import React, { Suspense, useContext, useEffect, useState } from "react";
import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { ContentLoading } from "../../../components/loader/ContentLoading";
import { getI18N } from "../../../utilities/i18n-utilities";
import { SPACE_MD } from "../../../styles/spacing";

const Tree = React.lazy(() => import("./tree/Tree"));
const OutputFilePreviewContainer = React.lazy(() =>
  import("./OutputFilePreviewContainer")
);

const { Content, Sider } = Layout;

export default function AnalysisPhylogeneticTree(props) {
  const { analysisContext } = useContext(AnalysisContext);
  const [tree, setTree] = useState(null);

  const BASE_URL = `${window.PAGE.base}/tree`;
  const pathRegx = new RegExp(/([a-zA-Z_]+)$/);

  // On load gets the phylogenetic tree
  useEffect(() => {}, []);

  /*
   * The following renders the components for the Phylogenetic Tree tabs
   */
  return tree === null ? (
    <Layout>
      <Sider width={200} style={{ background: "#fff" }}>
        <Location>
          {props => {
            const keyname = props.location.pathname.match(pathRegx);
            return (
              <Menu
                mode="vertical"
                selectedKeys={[keyname ? keyname[1] : "tree"]}
              >
                <Menu.Item key="tree">
                  <Link to={`${BASE_URL}/tree_preview`}>
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

      <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: "white" }}>
        <Content>
          <Suspense fallback={<ContentLoading />}>
            <Router>
              <Tree path={`${BASE_URL}/tree_preview`} />
              <OutputFilePreviewContainer path={`${BASE_URL}/file_preview`} />
            </Router>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  ) : (
    <ContentLoading />
  );
}
