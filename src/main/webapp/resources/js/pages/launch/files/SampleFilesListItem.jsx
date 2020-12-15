import React from "react";
import { Button, List, Radio } from "antd";
import { grey2 } from "../../../styles/colors";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import styled from "styled-components";
import { SPACE_XS } from "../../../styles/spacing";

const RadioItem = styled.button`
  padding: ${SPACE_XS};
  transition: all ease-in 0.3s;
  border: 1px dashed transparent;
  display: flex;
  justify-content: space-between;
  width: 100%;
  background-color: transparent;

  &:hover {
    background-color: ${grey2};
    border: 1px dashed rgb(217, 217, 217);
    cursor: pointer;
  }
`;

/**
 * React component to display the list of files that can be run on the current
 * pipeline for a given sample.
 *
 * @param {object} sample - the sample to display files for.
 * @param {function} removeSample - function to remove the sample from the cart
 * @param {function} updateSelectedFiles - function to call if user want to run a different set of files
 * @returns {JSX.Element}
 * @constructor
 */
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
          <SampleDetailViewer sampleId={sample.id}>
            <Button size="small">{sample.label}</Button>
          </SampleDetailViewer>
        }
        description={
          selected !== undefined ? (
            <div
              style={{
                overflowX: "auto",
              }}
            >
              <Radio.Group value={selected} onChange={updateSelected}>
                {sample.files.map((file) => (
                  <RadioItem key={`pf-${file.identifier}`}>
                    <Radio
                      key={`file-${file.identifier}`}
                      value={file.identifier}
                    >
                      {file.label}
                    </Radio>
                  </RadioItem>
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
