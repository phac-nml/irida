import React from "react";
import { Button, List, Radio } from "antd";
import { grey1, grey3 } from "../../../styles/colors";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { BlockRadioInput } from "../../../components/ant.design/forms/BlockRadioInput";
import { SPACE_XS } from "../../../styles/spacing";
import { BORDERED_LIGHT } from "../../../styles/borders";

/**
 * React component to display the list of assemblies that can be run on the current
 * pipeline for a given sample.
 *
 * @param {object} sample - the sample to display files for.
 * @param {function} removeSample - function to remove the sample from the cart
 * @param {function} updateSelectedFiles - function to call if user want to run a different set of files
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleAssembliesListItem({
  sample,
  style,
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
          <SampleDetailViewer
            sampleId={sample.id}
            removeSample={() => removeSample(sample)}
          >
            <Button size="small" style={{ marginLeft: SPACE_XS }}>
              {sample.label}
            </Button>
          </SampleDetailViewer>
        }
        description={
          sample.selected !== undefined ? (
            <Radio.Group style={{ width: `100%` }} value={sample.selected}>
              {sample.assemblyFiles.map((assemblyFile) => (
                <BlockRadioInput
                  key={`pf-${assemblyFile.identifier}`}
                  onClick={(e) => updateSelected(e, assemblyFile.identifier)}
                >
                  <Radio
                    key={`file-${assemblyFile.identifier}`}
                    value={assemblyFile.identifier}
                  >
                    {assemblyFile.label}
                  </Radio>
                </BlockRadioInput>
              ))}
            </Radio.Group>
          ) : (
            <div style={{ marginLeft: SPACE_XS }}>
              {i18n("SampleAssembliesListItem.no-assemblies")}
            </div>
          )
        }
      />
    </List.Item>
  );
}
