import { Button, Checkbox } from "antd";
import React from "react";
import styled from "styled-components";
import { grey8 } from "../../../../styles/colors";

const OnlyButton = styled(Button)`
  color: ${grey8};
  border-color: ${grey8};
  background-color: transparent;

  :hover {
    background-color: transparent;
  }
`;

export function MetadataFieldSelect({ checked, field, onChange }) {
  const [onlyBtnVisible, setOnlyBtnVisible] = React.useState(false);

  return (
    <div
      style={{
        width: "100%",
        height: 24,
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
      onClick={(event) => {
        event.stopPropagation();
      }}
      onMouseEnter={() => setOnlyBtnVisible(true)}
      onMouseLeave={() => setOnlyBtnVisible(false)}
    >
      <Checkbox
        checked={checked}
        onChange={(event) => onChange(event.target.checked, false)}
        style={{ width: "100%" }}
      >
        {field}
      </Checkbox>
      {onlyBtnVisible && (
        <OnlyButton size="small" onClick={() => onChange(true, true)}>
          {i18n("visualization.phylogenomics.metadata.fields.only")}
        </OnlyButton>
      )}
    </div>
  );
}
