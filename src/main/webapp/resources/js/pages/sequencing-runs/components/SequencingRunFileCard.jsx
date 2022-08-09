import React from "react";
import { useDrag } from "react-dnd";
import { Card } from "antd";
import styled from "styled-components";

const StyledCard = styled(Card)`
  &:hover {
    cursor: pointer;
  }
  .ant-card-body {
    padding: 5px;
  }
`;

/**
 * React component to render a drag'n'drop card.
 * @param {object} file - the file that is stored within the card
 * @param {number} sampleIndex - if moving from another sample, the index of the sample array
 * @param {number} pairIndex - if moving from another sample, the index of the file pair array inside the sample
 * @param {object} children - content to display in card
 * @param {object} props - any other attributes to add
 * @returns {JSX.Element} - Returns a card component
 */
export function SequencingRunFileCard({
  file,
  sampleIndex = null,
  pairIndex = null,
  children,
  ...props
}) {
  const [{}, drag] = useDrag({
    type: "card",
    item: { file, prevSampleIndex: sampleIndex, prevPairIndex: pairIndex },
  });

  return (
    <StyledCard ref={drag} {...props}>
      {children}
    </StyledCard>
  );
}
