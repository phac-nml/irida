import { List, Space } from "antd";
import React from "react";
import { LegendSectionItem } from "./LegendSectionItem";

export function LegendSection({
  title,
  sectionColourMap,
  onSectionItemColourChange,
}) {
  return (
    <Space direction="vertical" style={{ width: "100%" }}>
      <List.Item.Meta
        title={i18n(
          "visualization.phylogenomics.sidebar.legend.colour-by",
          title
        )}
      />
      {sectionColourMap
        ? Object.keys(sectionColourMap).map((key) => (
            <LegendSectionItem
              key={key}
              label={key}
              colour={sectionColourMap[key]}
              onChange={(colour) => onSectionItemColourChange(key, colour)}
            />
          ))
        : null}
    </Space>
  );
}
