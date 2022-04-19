import React from "react";
import { useDrag, useDrop } from "react-dnd";

/**
 * @file Used for Drag and Drop effect for rows in an Ant Design table using react dnd.
 * (see file src/main/webapp/resources/js/components/ant.design/DnDTable/index.js)
 * This file is responsible for the styling of the row when dragged and dropped.
 */

const type = "DraggableBodyRow";

/**
 * React component for drag and drop table rows.
 * @param {number} index- index of the current row
 * @param {function} moveRow - What do do on drop and the row orders need to update
 * @param {string} className - default class for the row
 * @param {Object} style - styles to apply to row based on current state (drag, drop, normal).
 * @param {Array} restProps - any other properties passed by the table
 * @returns {JSX.Element}
 * @constructor
 * @see https://ant.design/components/table/#components-table-demo-drag-sorting
 */
export const DraggableBodyRow = ({
  index,
  moveRow,
  className,
  style,
  ...restProps
}) => {
  const ref = React.useRef();
  const [{ isOver, dropClassName }, drop] = useDrop({
    accept: type,
    collect: (monitor) => {
      const { index: dragIndex } = monitor.getItem() || {};
      if (dragIndex === index) {
        return {};
      }
      return {
        isOver: monitor.isOver(),
        dropClassName:
          dragIndex < index ? " drop-over-downward" : " drop-over-upward",
      };
    },
    drop: (item) => {
      moveRow(item.index, index);
    },
  });
  const [, drag] = useDrag({
    type,
    item: { index },
    collect: (monitor) => ({
      isDragging: monitor.isDragging(),
    }),
  });
  drop(drag(ref));

  return (
    <tr
      ref={ref}
      className={`${className}${isOver ? dropClassName : ""}`}
      style={{ cursor: "move", ...style }}
      {...restProps}
    />
  );
};
