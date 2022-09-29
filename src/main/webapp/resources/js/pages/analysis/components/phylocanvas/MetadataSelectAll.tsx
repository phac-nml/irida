import { Switch, Typography } from "antd";
import React from "react";

type MetadataSelectAllProps = {
  checked: boolean;
  onChange: (checked: boolean) => void;
};

/**
 * React component to display / select whether all metadata fields
 * should be displayed along with the tree.
 * @param checked - true if all fields should be displayed else false
 * @param onChange - function to call when checked is changed
 * @constructor
 */
export function MetadataSelectAll({
  checked,
  onChange,
}: MetadataSelectAllProps) {
  return (
    <div
      style={{
        width: "100%",
        height: "100%",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
      onClick={(event) => {
        event.stopPropagation();
        onChange(!checked);
      }}
    >
      <Typography.Text>
        {i18n("visualization.phylogenomics.metadata.fields.select-all")}
      </Typography.Text>
      <Switch
        checked={checked}
        onChange={(checked) => onChange(checked)}
        size="small"
      />
    </div>
  );
}
