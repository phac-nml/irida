import React from "react";
import { Button, List, Radio } from "antd";
import { grey2 } from "../../../styles/colors";
import { SampleDetailSidebar } from "../../../components/samples/SampleDetailSidebar";
import styled from "styled-components";

const BlockRadio = styled(Radio)`
  display: block;
  height: 30px;
  line-height: 30px;
  font-weight: normal;
`;

export function SampleFilesListItem({
  sample,
  removeSample,
  updateSelectedFiles,
}) {
  const [selected, setSelected] = React.useState(undefined);

  React.useEffect(() => {
    if (sample.files.length !== 0) {
      setSelected(sample.files[0].identifier);
    }
  }, [sample.files]);

  const updateSelected = (e) => {
    const id = e.target.value;
    updateSelectedFiles(selected, id);
    setSelected(id);
  };

  return (
    <List.Item
      actions={[
        <Button type="link" key={`remove`} onClick={() => removeSample(sample)}>
          {i18n("SampleFilesListItem.remove")}
        </Button>,
      ]}
      style={{
        backgroundColor: selected ? "transparent" : grey2,
      }}
    >
      <List.Item.Meta
        title={
          <SampleDetailSidebar sampleId={sample.id}>
            <Button size="small">{sample.label}</Button>
          </SampleDetailSidebar>
        }
        description={
          selected != undefined ? (
            <div
              style={{
                overflowX: "auto",
              }}
            >
              <Radio.Group value={selected} onChange={updateSelected}>
                {sample.files.map((file) => (
                  <BlockRadio
                    key={`file-${file.identifier}`}
                    value={file.identifier}
                  >
                    {file.label}
                  </BlockRadio>
                ))}
              </Radio.Group>
            </div>
          ) : (
            <div>{i18n("SampleFilesListItem.no-files")}</div>
          )
        }
      />
    </List.Item>
  );
}
