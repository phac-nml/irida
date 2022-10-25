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

type LegendSectionItemProps = {
  label: string;
  colour: string;
  onChange: (colour: string) => void;
};

/**
 * React component to render a metadata field for colour selection
 * @param label - Metadata field name
 * @param initialColour - starting colour of the field
 * @param onChange - Function to do the change
 * @constructor
 */
export function LegendSectionItem({
  label,
  colour: initialColour,
  onChange,
}: LegendSectionItemProps): JSX.Element {
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
