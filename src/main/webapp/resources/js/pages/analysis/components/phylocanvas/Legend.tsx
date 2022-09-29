import { List, Typography } from "antd";
import React from "react";
import { downloadObjectURL } from "../../../../utilities/file-utilities";
import { setMetadataColourForTermWithValue } from "../../redux/treeSlice";
import { useAppDispatch, useAppSelector } from "../../store";
import { exportLegendSVG } from "../../tree-utilities";
import {
  HandleLegendSectionDownload,
  LegendDownloadMenu,
} from "./LegendDownloadMenu";
import { LegendSection } from "./LegendSection";

/**
 * React component to render a legend for the phylocanvas viewer
 */
export function Legend(): JSX.Element {
  const { metadataColourMap, terms, treeProps } = useAppSelector(
    (state) => state.tree
  );
  const dispatch = useAppDispatch();

  const onItemClick: HandleLegendSectionDownload = (term: string) => {
    const blob = exportLegendSVG(term, metadataColourMap, treeProps);
    const url = window.URL.createObjectURL(blob);
    downloadObjectURL(url, `${term}-legend.svg`);
  };

  return (
    <div
      style={{
        paddingLeft: 14,
        paddingRight: 14,
        height: "inherit",
        overflow: "auto",
        position: "relative",
      }}
    >
      <div
        style={{
          position: "absolute",
          zIndex: 4,
          height: 0,
          width: 32,
          top: 4,
          right: 4,
          userSelect: "none",
        }}
      >
        <div style={{ position: "fixed" }}>
          <LegendDownloadMenu terms={terms} onItemClick={onItemClick} />
        </div>
      </div>
      <List
        header={
          <Typography.Text style={{ paddingTop: 12, paddingBottom: 12 }}>
            {i18n("visualization.phylogenomics.sidebar.legend.title")}
          </Typography.Text>
        }
        dataSource={terms}
        renderItem={(item) => (
          <List.Item style={{ width: "100%" }}>
            <LegendSection
              title={item}
              sectionColourMap={metadataColourMap[item]}
              onSectionItemColourChange={(key, colour) =>
                dispatch(
                  setMetadataColourForTermWithValue({ item, key, colour })
                )
              }
            />
          </List.Item>
        )}
      />
    </div>
  );
}
