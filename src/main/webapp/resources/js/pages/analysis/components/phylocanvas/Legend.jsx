import { List, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { setMetadataColourForTermWithValue } from "../../redux/treeSlice";
import { LegendDownloadMenu } from "./LegendDownloadMenu";
import { LegendSection } from "./LegendSection";

export function Legend() {
  const { metadataColourMap, terms } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  const onItemClick = (term, format) => {
    console.log(`Downloading ${term} as ${format}`);
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
