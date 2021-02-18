import React from "react";
import { PageHeader, Space, Table, Typography } from "antd";
import { createDndContext, DndProvider, useDrag, useDrop } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { navigate } from "@reach/router";
import { getMetadataTemplate } from "../../../apis/metadata/metadata-templates";
import update from "immutability-helper";
import styled from "styled-components";

const { Paragraph, Text } = Typography;

const RNDContext = createDndContext(HTML5Backend);

const DraggableTable = styled(Table)`
  tr.drop-over-downward td {
    border-bottom: 2px dashed #1890ff;
  }

  tr.drop-over-upward td {
    border-top: 2px dashed #1890ff;
  }
`;

const type = "DragableBodyRow";

const DragableBodyRow = ({
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
    item: { type, index },
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

const columns = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
  },
  {
    title: "Age",
    dataIndex: "age",
    key: "age",
  },
  {
    title: "Address",
    dataIndex: "address",
    key: "address",
  },
];

const dataSource = [
  {
    key: "1",
    name: "John Brown",
    age: 32,
    address: "New York No. 1 Lake Park",
    index: 0,
  },
  {
    key: "2",
    name: "Jim Green",
    age: 42,
    address: "London No. 1 Lake Park",
    index: 1,
  },
  {
    key: "3",
    name: "Joe Black",
    age: 32,
    address: "Sidney No. 1 Lake Park",
    index: 2,
  },
];

export function MetadataTemplate({ id }) {
  const [template, setTemplate] = React.useState({});

  const [data, setData] = React.useState(dataSource);

  React.useEffect(() => {
    getMetadataTemplate(id).then((data) => setTemplate(data));
  }, []);

  const onChange = (field, text) => {
    console.log(field, text);
  };

  const moveRow = React.useCallback(
    (dragIndex, hoverIndex) => {
      const dragRow = data[dragIndex];
      setData(
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

  const manager = React.useRef(RNDContext);

  return (
    <PageHeader title={template.name} onBack={() => navigate("./")}>
      <Space direction="vertical" style={{ width: `100%` }}>
        <Text strong>Description</Text>
        <Paragraph
          editable={{ onChange: (text) => onChange("description", text) }}
        >
          {template.description || ""}
        </Paragraph>
        <DndProvider manager={manager.current.dragDropManager}>
          <DraggableTable
            pagination={false}
            dataSource={data}
            columns={columns}
            components={{
              body: {
                row: DragableBodyRow,
              },
            }}
            onRow={(record, index) => ({
              index,
              moveRow,
            })}
          />
        </DndProvider>
      </Space>
    </PageHeader>
  );
}
