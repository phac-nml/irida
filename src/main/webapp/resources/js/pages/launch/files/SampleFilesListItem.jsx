import React from "react";
import { Button, List, Radio } from "antd";
import { grey1, grey3 } from "../../../styles/colors";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { BlockRadioInput } from "../../../components/ant.design/forms/BlockRadioInput";
import { SPACE_XS } from "../../../styles/spacing";
import { BORDERED_LIGHT } from "../../../styles/borders";

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
  style,
  projectId,
  removeSample,
  updateSelectedFiles,
}) {
  const updateSelected = (e, id) => {
    e.preventDefault();
    updateSelectedFiles(sample, id);
  };

  return (
    <List.Item
      actions={[
        <Button type="link" key={`remove`} onClick={() => removeSample(sample)}>
          {i18n("SampleFilesListItem.remove")}
        </Button>,
      ]}
      style={{
        backgroundColor: sample.selected ? grey1 : grey3,
        boxSizing: `border-box`,
        borderBottom: BORDERED_LIGHT,
        ...style,
      }}
    >
      <List.Item.Meta
        title={
          <SampleDetailViewer sampleId={sample.id} projectId={projectId}>
            <Button size="small" style={{ marginLeft: SPACE_XS }}>
              {sample.label}
            </Button>
          </SampleDetailViewer>
        }
        description={
          sample.selected !== undefined ? (
            <Radio.Group style={{ width: `100%` }} value={sample.selected}>
              {sample.files.map((file) => (
                <BlockRadioInput
                  key={`pf-${file.fileInfo.identifier}`}
                  onClick={(e) => updateSelected(e, file.fileInfo.identifier)}
                >
                  <Radio
                    key={`file-${file.fileInfo.identifier}`}
                    value={file.fileInfo.identifier}
                  >
                    {file.fileInfo.label}
                  </Radio>
                </BlockRadioInput>
              ))}
            </Radio.Group>
          ) : (
            <div style={{ marginLeft: SPACE_XS }}>
              {i18n("SampleFilesListItem.no-files")}
            </div>
          )
        }
      />
    </List.Item>
  );
}
