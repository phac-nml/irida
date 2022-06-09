import React from "react";
import { useDrop } from "react-dnd";
import { List } from "antd";

/**
 * React component to render a drag'n'drop list.
 * @param {string} name - name of the drag'n'drop item
 * @param {string} type - type of the drag'n'drop item
 * @param {object} children - content to display in list
 * @param {object} props - any other attributes to add
 * @returns {JSX.Element} - Returns a list component
 */
export function DnDList({ name, type = "card", children, ...props }) {
  const [{ canDrop, isOver }, drop] = useDrop({
    accept: type,
    drop: () => ({ name }),

    // collect: (monitor) => ({
    //   isOver: monitor.isOver(),
    //   canDrop: monitor.canDrop(),
    // }),
  });

  // console.log("drop: " + name);
  // console.log("options", { canDrop, isOver });

  return (
    <div ref={drop}>
      <List {...props}>{children}</List>
    </div>
  );
}
