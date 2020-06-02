import React, { useRef, useState } from "react";
import { useMetadataTemplate } from "../../../../contexts/metadata-template-context";
import { Button } from "antd";

import { HotTable } from "@handsontable/react";
import "handsontable/dist/handsontable.css";
import { SPACE_SM } from "../../../../styles/spacing";

export function Template() {
  const { template } = useMetadataTemplate();
  const [fields, setFields] = useState(template.fields.map((f) => f.label));
  const hotRef = useRef();

  const settings = {
    data: [fields],
    width: "100%",
    colWidths: 140,
    licenseKey: "non-commercial-and-evaluation",
    minSpareRows: 0,
    colHeaders: true,
    manualColumnMove: true,
    allowRemoveColumn: true,
    allowInsertColumn: true,
    contextMenu: {
      items: {
        col_left: {
          disabled: false,
        },
        col_right: {
          disabled: false,
        },
        remove_col: {
          name: "Remove Field",
          disabled: false,
          callback(key, selections) {
            const f = [...fields];
            selections.forEach((s) => {
              f.splice(s.start.col, 1);
            });
            setFields(f);
          },
        },
      },
    },
    stretchH: "all",
  };

  const getFields = () => {
    const hot = hotRef.current.hotInstance;
    console.log(hot.getDataAtRow(0));

    const settings = hot.getSettings();
    const colHeadersArray = hot.getSettings().colHeaders;
    console.log(settings);
    // colHeadersArray.push("Price #2");
    // settings["colHeaders"] = colHeadersArray;

    hot.updateSettings(settings);
  };

  return (
    <div
      className="hot handsontable htColumnHeaders"
      style={{
        width: "100%",
        overflow: "hidden",
        border: `1px solid transparent`,
      }}
    >
      <div style={{ marginBottom: SPACE_SM }}>
        <Button onClick={getFields}>New Field Group</Button>
      </div>
      <HotTable ref={hotRef} settings={settings} />
    </div>
  );
}
