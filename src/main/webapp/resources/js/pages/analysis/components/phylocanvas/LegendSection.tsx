import { List, Space } from "antd";
import React, { useMemo } from "react";
import { LegendSectionItem } from "./LegendSectionItem";
import { sortLegendSection } from "../../tree-utilities";

type LegendSectionProps = {
  title: string;
  sectionColourMap: Record<string, string>;
  onSectionItemColourChange: (key: string, colour: string) => void;
};

const BLANK_LABEL = i18n("visualization.phylogenomics.metadata.fields.blank");

/**
 * React component to display a section of the legend of the phylocanvas
 */
export function LegendSection({
  title,
  sectionColourMap,
  onSectionItemColourChange,
}: LegendSectionProps): JSX.Element {
  const sectionItems = useMemo(
    () =>
      Object.keys(sectionColourMap)
        .sort(sortLegendSection)
        .map((field) => (
          <LegendSectionItem
            key={field}
            label={field === "" ? BLANK_LABEL : field}
            colour={sectionColourMap[field]}
            onChange={(colour: string) =>
              onSectionItemColourChange(field, colour)
            }
          />
        )),
    [onSectionItemColourChange, sectionColourMap]
  );

  return (
    <Space direction="vertical" style={{ width: "100%" }}>
      <List.Item.Meta
        title={i18n(
          "visualization.phylogenomics.sidebar.legend.colour-by",
          title
        )}
      />
      {sectionItems}
    </Space>
  );
}
