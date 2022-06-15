import React from "react";
import { useDrag } from "react-dnd";
import { Card } from "antd";

/**
 * React component to render a drag'n'drop card.
 * @param {object} file - the file that is being stored in the card
 * @param {number} index - the index of the array the card is moving from
 * @param {object} children - content to display in card
 * @param {object} props - any other attributes to add
 * @returns {JSX.Element} - Returns a card component
 */
export function SequencingRunFileCard({
  file,
  index = null,
  children,
  ...props
}) {
  const [{ isDragging }, drag] = useDrag({
    type: "card",
    item: { file, prevIndex: index },
  });

  return (
    <Card ref={drag} {...props}>
      {children}
    </Card>
  );
}
