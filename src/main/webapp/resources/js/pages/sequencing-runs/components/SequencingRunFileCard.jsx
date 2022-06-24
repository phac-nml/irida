import React from "react";
import { useDrag } from "react-dnd";
import { Card } from "antd";

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
  const [{ isDragging }, drag] = useDrag({
    type: "card",
    item: { file, prevSampleIndex: sampleIndex, prevPairIndex: pairIndex },
  });

  return (
    <Card ref={drag} {...props}>
      {children}
    </Card>
  );
}
