import styled from "styled-components";
import { Table } from "antd";
import { blue6 } from "../../../styles/colors";
import React from "react";
import { createDndContext, DndProvider } from "react-dnd";
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

const RNDContext = createDndContext(HTML5Backend);

export default function DnDTable({
  size = "default",
  data,
  columns,
  onRowUpdate = Function.prototype,
  ...props
}) {
  const manager = React.useRef(RNDContext);

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
    <DndProvider manager={manager.current.dragDropManager}>
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
