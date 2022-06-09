import React from "react";
import { useDrag } from "react-dnd";
import { Card } from "antd";

/**
 * React component to render a drag'n'drop card.
 * @param {function} setList - the function that sets the list
 * @param {number} id - id of the drag'n'drop item
 * @param {string} title - title of the card
 * @param {object} children - content to display in card
 * @param {object} props - any other attributes to add
 * @returns {JSX.Element} - Returns a card component
 */
export function DnDCard({ setList, id, children, ...props }) {
  const changeItemList = (currentItem, ListName) => {
    setList((prevState) => {
      return prevState.map((e) => {
        return {
          ...e,
          list: e.id === currentItem.id ? ListName : e.list,
        };
      });
    });
  };

  const [{ isDragging }, drag] = useDrag({
    type: "card",
    item: { id },
    end: (item, monitor) => {
      const dropResult = monitor.getDropResult();
      if (dropResult) {
        const { name } = dropResult;
        // console.log(item);
        // console.log(name);
        changeItemList(item, name);
      }
    },
    collect: (monitor) => ({
      isDragging: monitor.isDragging(),
    }),
  });

  return (
    <Card ref={drag} {...props}>
      {children}
    </Card>
  );
}
