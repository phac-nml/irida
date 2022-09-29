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

const FieldCheckbox = styled(Checkbox)`
  min-width: 0;

  span:nth-child(2) {
    overflow: hidden;
    text-overflow: ellipsis;
  }
`;

type MetadataFieldSelectProps = {
  checked: boolean;
  field: string;
  width: number;
  onChange: (checked: boolean, only: boolean) => void;
};

export function MetadataFieldSelect({
  checked,
  field,
  width,
  onChange,
}: MetadataFieldSelectProps): JSX.Element {
  const [onlyBtnVisible, setOnlyBtnVisible] = React.useState<boolean>(false);

  return (
    <div
      style={{
        height: 24,
        width,
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
      <FieldCheckbox
        checked={checked}
        value={field}
        onChange={(event) => onChange(event.target.checked, false)}
      >
        <span title={field}>{field}</span>
      </FieldCheckbox>
      <OnlyButton
        size="small"
        title={i18n("visualization.phylogenomics.metadata.fields.only-hint")}
        onClick={() => onChange(true, true)}
        style={{ display: onlyBtnVisible ? "inline-block" : "none" }}
      >
        {i18n("visualization.phylogenomics.metadata.fields.only")}
      </OnlyButton>
    </div>
  );
}
