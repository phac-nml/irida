import React, { useEffect, useContext } from "react";
import { Collapse, Col, Tree, Descriptions, Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { getAnalysisProvenanceByFile } from "../../../apis/analysis/analysis";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { AnalysisContext } from "../../../contexts/AnalysisContext";

const { TreeNode } = Tree;
const { Panel } = Collapse;
const { Content } = Layout;

const treeData = [
  {
    title: "VCF 2 snvalignment",
    key: "0-0",
    children: [
      {
        title: (
          <Descriptions bordered>
            <Descriptions.Item label="Reference">"reference"</Descriptions.Item>
            <Descriptions.Item label="strain_list.select_list">
              all
            </Descriptions.Item>
          </Descriptions>
        )
      },
      {
        title: "Consolidate VCFs",
        key: "0-0-1-0",
        children: [
          {
            title: (
              <Descriptions bordered>
                <Descriptions.Item label="coverage">"15"</Descriptions.Item>
                <Descriptions.Item label="mean_mapping">"30"</Descriptions.Item>
                <Descriptions.Item label="use_density_filter.window_size">
                  500
                </Descriptions.Item>
                <Descriptions.Item label="snv_abundance_ratio">
                  "0.75"
                </Descriptions.Item>
                <Descriptions.Item label="use_density_filter.select_list">
                  yes
                </Descriptions.Item>
                <Descriptions.Item label="use_density_filter.threshold">
                  2
                </Descriptions.Item>
              </Descriptions>
            )
          }
        ]
      },
      {
        title: "bcftools_view",
        key: "0-0-2-0",
        children: [
          {
            title: (
              <Col span={24}>
                <Descriptions bordered>
                  <Descriptions.Item label="max_nref"></Descriptions.Item>
                  <Descriptions.Item label="private">null</Descriptions.Item>
                  <Descriptions.Item label="min_nref"></Descriptions.Item>
                  <Descriptions.Item label="output_format">
                    "b"
                  </Descriptions.Item>
                  <Descriptions.Item label="sites_no_genotype">
                    "off"
                  </Descriptions.Item>
                  <Descriptions.Item label="select_sites">
                    null
                  </Descriptions.Item>
                  <Descriptions.Item label="trim_alt_alleles">
                    "False"
                  </Descriptions.Item>
                  <Descriptions.Item label="include_types">
                    null
                  </Descriptions.Item>
                  <Descriptions.Item label="filters"></Descriptions.Item>
                  <Descriptions.Item label="region"></Descriptions.Item>
                  <Descriptions.Item label="header_option">
                    "all"
                  </Descriptions.Item>
                  <Descriptions.Item label="samples"></Descriptions.Item>
                </Descriptions>
              </Col>
            )
          }
        ]
      }
    ]
  }
];

export default function AnalysisProvenance() {
  const { analysisContext } = useContext(AnalysisContext);

  useEffect(() => {
    //getAnalysisProvenanceByFile(analysisContext.analysis.identifier).then(data => {});
  }, []);

  function renderTreeNodes(data) {
    let a = [];
    let t = 0;
    data.map(item => {
      if (item.children) {
        t = t + 2;
        a.push(
          <TreeNode
            title={item.title}
            key={item.key}
            dataRef={item}
            style={{ marginLeft: `${t * 5}px`, marginBottom: "50px" }}
          >
            {renderTreeNodes(item.children)}
          </TreeNode>
        );
      } else {
        t = 0;
        a.push(
          <TreeNode
            style={{ marginLeft: "5px", marginBottom: "50px" }}
            key={item.key}
            {...item}
          />
        );
      }
    });
    return a;
  }

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent title="Provenance">
        <Collapse accordion>
          <Panel
            header="vcf2core.tsv"
            key="1"
            style={{ backgroundColor: grey1 }}
          >
            <Collapse bordered={false}>
              <Panel
                header="VCF 2 snvalignment"
                style={{
                  backgroundColor: grey1
                }}
              >
                <Descriptions bordered>
                  <Descriptions.Item label="Reference">
                    "reference"
                  </Descriptions.Item>
                  <Descriptions.Item label="strain_list.select_list">
                    all
                  </Descriptions.Item>
                </Descriptions>
              </Panel>
              <Collapse bordered={false}>
                <Panel
                  header="Consolidate VCFs"
                  style={{
                    backgroundColor: grey1,
                    marginLeft: "10px",
                    marginTop: "5px"
                  }}
                >
                  <Descriptions bordered>
                    <Descriptions.Item label="coverage">"15"</Descriptions.Item>
                    <Descriptions.Item label="mean_mapping">
                      "30"
                    </Descriptions.Item>
                    <Descriptions.Item label="use_density_filter.window_size">
                      500
                    </Descriptions.Item>
                    <Descriptions.Item label="snv_abundance_ratio">
                      "0.75"
                    </Descriptions.Item>
                    <Descriptions.Item label="use_density_filter.select_list">
                      yes
                    </Descriptions.Item>
                    <Descriptions.Item label="use_density_filter.threshold">
                      2
                    </Descriptions.Item>
                  </Descriptions>
                  <Collapse bordered={false}>
                    <Panel
                      header="bcftools_view"
                      style={{
                        backgroundColor: grey1,
                        marginTop: "5px",
                        marginLeft: "2px"
                      }}
                    >
                      <Descriptions bordered>
                        <Descriptions.Item label="max_nref"></Descriptions.Item>
                        <Descriptions.Item label="private">
                          null
                        </Descriptions.Item>
                        <Descriptions.Item label="min_nref"></Descriptions.Item>
                        <Descriptions.Item label="output_format">
                          "b"
                        </Descriptions.Item>
                        <Descriptions.Item label="sites_no_genotype">
                          "off"
                        </Descriptions.Item>
                        <Descriptions.Item label="select_sites">
                          null
                        </Descriptions.Item>
                        <Descriptions.Item label="trim_alt_alleles">
                          "False"
                        </Descriptions.Item>
                        <Descriptions.Item label="include_types">
                          null
                        </Descriptions.Item>
                        <Descriptions.Item label="filters"></Descriptions.Item>
                        <Descriptions.Item label="region"></Descriptions.Item>
                        <Descriptions.Item label="header_option">
                          "all"
                        </Descriptions.Item>
                        <Descriptions.Item label="samples"></Descriptions.Item>
                      </Descriptions>
                    </Panel>
                  </Collapse>
                </Panel>
              </Collapse>
            </Collapse>
          </Panel>
          <Panel
            header="phylogeneticTreeStats.txt"
            key="2"
            style={{ backgroundColor: grey1 }}
          >
            <Collapse bordered={false}>
              <Panel
                header="VCF 2 snvalignment"
                style={{
                  backgroundColor: grey1,
                  marginLeft: "2px"
                }}
              >
                <Descriptions bordered>
                  <Descriptions.Item label="Reference">
                    "reference"
                  </Descriptions.Item>
                  <Descriptions.Item label="strain_list.select_list">
                    all
                  </Descriptions.Item>
                </Descriptions>
              </Panel>
              <Collapse bordered={false}>
                <Panel
                  header="Consolidate VCFs"
                  style={{
                    backgroundColor: grey1,
                    marginLeft: "4px",
                    marginTop: "5px"
                  }}
                >
                  <Descriptions bordered>
                    <Descriptions.Item label="coverage">"15"</Descriptions.Item>
                    <Descriptions.Item label="mean_mapping">
                      "30"
                    </Descriptions.Item>
                    <Descriptions.Item label="use_density_filter.window_size">
                      500
                    </Descriptions.Item>
                    <Descriptions.Item label="snv_abundance_ratio">
                      "0.75"
                    </Descriptions.Item>
                    <Descriptions.Item label="use_density_filter.select_list">
                      yes
                    </Descriptions.Item>
                    <Descriptions.Item label="use_density_filter.threshold">
                      2
                    </Descriptions.Item>
                  </Descriptions>
                  <Collapse bordered={false}>
                    <Panel
                      header="bcftools_view"
                      style={{
                        backgroundColor: grey1,
                        marginTop: "5px",
                        marginLeft: "6px"
                      }}
                    >
                      <Descriptions bordered>
                        <Descriptions.Item label="max_nref"></Descriptions.Item>
                        <Descriptions.Item label="private">
                          null
                        </Descriptions.Item>
                        <Descriptions.Item label="min_nref"></Descriptions.Item>
                        <Descriptions.Item label="output_format">
                          "b"
                        </Descriptions.Item>
                        <Descriptions.Item label="sites_no_genotype">
                          "off"
                        </Descriptions.Item>
                        <Descriptions.Item label="select_sites">
                          null
                        </Descriptions.Item>
                        <Descriptions.Item label="trim_alt_alleles">
                          "False"
                        </Descriptions.Item>
                        <Descriptions.Item label="include_types">
                          null
                        </Descriptions.Item>
                        <Descriptions.Item label="filters"></Descriptions.Item>
                        <Descriptions.Item label="region"></Descriptions.Item>
                        <Descriptions.Item label="header_option">
                          "all"
                        </Descriptions.Item>
                        <Descriptions.Item label="samples"></Descriptions.Item>
                      </Descriptions>
                    </Panel>
                  </Collapse>
                </Panel>
              </Collapse>
            </Collapse>
          </Panel>
        </Collapse>
      </TabPaneContent>
    </Layout>
  );
}
