import React, { useRef, useState } from "react";
import { useMetadataTemplate } from "../../../../contexts/metadata-template-context";
import { Button, Col, Row, Space, Table } from "antd";

import { HotTable } from "@handsontable/react";
import Handsontable from "handsontable";
import "handsontable/dist/handsontable.css";

const reducer = (state, action) => {
  switch (action.type) {
    case "ADD_FIELD":
      return {
        ...state,
        headers: [...state.headers].push("FOOBAR"),
        columns: [...state.columns].push({ data: "DS" }),
      };
  }
};

export function Template() {
  const { template } = useMetadataTemplate();
  const { fields } = template;
  const hotRef = useRef();

  const [colHeaders, setColHeaders] = useState([
    "ID",
    "First Name",
    "Last Name",
    "Address",
  ]);
  const [columns, setColumns] = useState([
    { data: "id" },
    { data: "name.first" },
    { data: "name.last" },
    { data: "address" },
  ]);

  const settings = {
    width: "100%",
    colWidths: 100,
    rowHeights: 50,
    licenseKey: "non-commercial-and-evaluation",
    data: [],
    dataSchema: { id: null, name: { first: null, last: null }, address: null },
    colHeaders,
    columns,
    minSpareRows: 0,
    rowHeaders: true,
  };

  const addNewGroup = () => {
    const t = [...colHeaders];
    t.push("FOOBAR");
    setColHeaders(t);
    const s = [...columns];
    s.push({ data: "foobar" });
    setColumns(s);
  };

  return (
    <div
      className="hot handsontable htRowHeaders htColumnHeaders"
      style={{ width: "100%", overflow: "hidden", border: `1px solid blue` }}
    >
      <Button onClick={addNewGroup}>New Field Group</Button>
      <HotTable
        width="100%"
        id="net-template"
        ref={hotRef}
        settings={settings}
      />
      {/*<Space direction="vertical">*/}
      {/*  <Space>*/}
      {/*  </Space>*/}
      {/*  <div style={{ width: 800 }}>*/}
      {/*  </div>*/}
      {/*</Space>*/}
    </div>
  );
}
