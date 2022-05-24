import { Space, Typography } from "antd";
import React from "react";
import styled from "styled-components";
import { useDebounce } from "../../../../hooks";

const ColourInput = styled.input.attrs({ type: "color" })`
  background-color: transparent;
  cursor: pointer;
  height: 2.5em;
  width: 2.5em;
  border: none;
  outline: none;
  -webkit-appearance: none;
`;

export function LegendItem({ label, colour: initialColour, onChange }) {
  const [colour, setColour] = React.useState(initialColour);

  const debouncedColour = useDebounce(colour, 500);

  React.useEffect(() => {
    onChange(debouncedColour);
  }, [debouncedColour, onChange]);

  return (
    <Space direction="horizontal">
      <ColourInput value={colour} onChange={(e) => setColour(e.target.value)} />
      <Typography.Text>{label}</Typography.Text>
    </Space>
  );
}
