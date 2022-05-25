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
        // prettier-ignore
        title={i18n("visualization.phylogenomics.sidebar.legend.colour-by", title)}
      />
      {sectionColourMap
        ? Object.keys(sectionColourMap)
            .slice()
            .sort((a, b) => {
              if (a === "") return 1;
              if (b === "") return -1;
              return a < b ? -1 : 1;
            })
            .map((key) => (
              <LegendSectionItem
                key={key}
                label={
                  key === ""
                    ? i18n("visualization.phylogenomics.metadata.fields.blank")
                    : key
                }
                colour={sectionColourMap[key]}
                onChange={(colour) => onSectionItemColourChange(key, colour)}
              />
            ))
        : null}
    </Space>
  );
}
