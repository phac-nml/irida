import { List, Space } from "antd";
import React from "react";
import { LegendSectionItem } from "./LegendSectionItem";

export function LegendSection({
  title,
  sectionColourMap,
  onSectionItemColourChange,
}) {
  const blankLabel = i18n("visualization.phylogenomics.metadata.fields.blank");
  return (
    <Space direction="vertical" style={{ width: "100%" }}>
      <List.Item.Meta
        // prettier-ignore
        title={i18n("visualization.phylogenomics.sidebar.legend.colour-by", title)}
      />
      {Object.keys(sectionColourMap)
        .sort((a, b) => {
          if (a === "") return 1;
          if (b === "") return -1;
          return a < b ? -1 : 1;
        })
        .map((key) => (
          <LegendSectionItem
            key={key}
            label={key === "" ? blankLabel : key}
            colour={sectionColourMap[key]}
            onChange={(colour) => onSectionItemColourChange(key, colour)}
          />
        ))}
    </Space>
  );
}
