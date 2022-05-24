import { Switch, Typography } from "antd";
import React from "react";

export function MetadataSelectAll({ checked, onChange }) {
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
