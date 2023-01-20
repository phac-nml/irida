import React from "react";
import { Input } from "antd";
import { MetadataItem } from "../../../../apis/projects/samples";

export function SampleMetadataImportReviewTableCell({
  text,
  item,
  sampleNameColumn,
}: {
  text: any;
  item: MetadataItem;
  sampleNameColumn: string;
}): JSX.Element {
  const [editing, setEditing] = React.useState(false);

  if (editing) {
    return (
      <Input
        defaultValue={item[sampleNameColumn]}
        onPressEnter={() => setEditing(false)}
        onBlur={() => setEditing(false)}
      />
    );
  } else {
    return (
      <div
        onClick={() => {
          setEditing(true);
        }}
      >
        {text}
      </div>
    );
  }
}
