import styled from "styled-components";
import { Table } from "antd";
import { blue6 } from "../../../styles/colors";
import React from "react";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import update from "immutability-helper";
import { DraggableBodyRow } from "./DraggableBodyRow";

const DraggableTable = styled(Table)`
  tr.drop-over-downward td {
    border-bottom: 2px dashed ${blue6};
  }

  tr.drop-over-upward td {
    border-top: 2px dashed ${blue6};
  }
`;

/**
 * React component to render and Ant Design table with drag and drop rows for sorting
 * @param {string} size - size of the table rows (from Ant Design, either default, middle, small)
 * @param {Array} data - dataSource for the table
 * @param {Array} columns - table columns
 * @param {function} onRowUpdate - Callback function to handle updating order of the data
 * @param {Array} props - any other properties passed to the table
 * @returns {JSX.Element}
 * @constructor
 * @see https://ant.design/components/table/#components-table-demo-drag-sorting
 */
export default function DnDTable({
  size = "default",
  data,
  columns,
  onRowUpdate = Function.prototype,
  ...props
}) {
  const moveRow = React.useCallback(
    (dragIndex, hoverIndex) => {
      const dragRow = data[dragIndex];
      onRowUpdate(
        update(data, {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        })
      );
    },
    [data]
  );

  return (
    <DndProvider backend={HTML5Backend}>
      <DraggableTable
        size={size}
        pagination={false}
        dataSource={data}
        columns={columns}
        components={{
          body: {
            row: DraggableBodyRow,
          },
        }}
        onRow={(record, index) => ({
          index,
          moveRow,
        })}
        {...props}
      />
    </DndProvider>
  );
}
