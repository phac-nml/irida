import React from "react";
import { Button, Radio } from "antd";
import { grey4 } from "../../../styles/colors";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { BlockRadioInput } from "../../../components/ant.design/forms/BlockRadioInput";
import { SPACE_XS } from "../../../styles/spacing";

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
  removeSample,
  updateSelectedFiles,
}) {
  const updateSelected = (e, id) => {
    e.preventDefault();
    updateSelectedFiles(sample, id);
  };

  return (
    <div
      key={`foobar-${sample.label}`}
      style={{
        padding: SPACE_XS,
        borderBottom: `1px solid ${grey4}`,
        ...style,
      }}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <SampleDetailViewer sampleId={sample.id}>
          <Button size="small">{sample.label}</Button>
        </SampleDetailViewer>
        <Button type="link" key={`remove`} onClick={() => removeSample(sample)}>
          {i18n("SampleFilesListItem.remove")}
        </Button>
      </div>
      {sample.selected ? (
        <Radio.Group style={{ width: `100%` }} value={sample.selected}>
          {sample.files.map((file) => (
            <BlockRadioInput
              key={`pf-${file.identifier}`}
              onClick={(e) => updateSelected(e, file.identifier)}
            >
              <Radio key={`file-${file.identifier}`} value={file.identifier}>
                {file.label}
              </Radio>
            </BlockRadioInput>
          ))}
        </Radio.Group>
      ) : (
        <div>{i18n("SampleFilesListItem.no-files")}</div>
      )}
      {/*<List.Item*/}
      {/*  actions={[*/}
      {/*    <Button*/}
      {/*      type="link"*/}
      {/*      key={`remove`}*/}
      {/*      onClick={() => removeSample(sample)}*/}
      {/*    >*/}
      {/*      {i18n("SampleFilesListItem.remove")}*/}
      {/*    </Button>,*/}
      {/*  ]}*/}
      {/*  style={{*/}
      {/*    backgroundColor: selected ? "transparent" : grey2,*/}
      {/*    borderBottom: `1px solid ${grey4}`,*/}
      {/*    ...style,*/}
      {/*  }}*/}
      {/*>*/}
      {/*  <List.Item.Meta*/}
      {/*    title={*/}
      {/*      <SampleDetailViewer sampleId={sample.id}>*/}
      {/*        <Button size="small">{sample.label}</Button>*/}
      {/*      </SampleDetailViewer>*/}
      {/*    }*/}
      {/*    description={*/}
      {/*      selected !== undefined ? (*/}
      {/*        <Radio.Group style={{ width: `100%` }} value={selected}>*/}
      {/*          {sample.files.map((file) => (*/}
      {/*            <BlockRadioInput*/}
      {/*              key={`pf-${file.identifier}`}*/}
      {/*              onClick={(e) => updateSelected(e, file.identifier)}*/}
      {/*            >*/}
      {/*              <Radio*/}
      {/*                key={`file-${file.identifier}`}*/}
      {/*                value={file.identifier}*/}
      {/*              >*/}
      {/*                {file.label}*/}
      {/*              </Radio>*/}
      {/*            </BlockRadioInput>*/}
      {/*          ))}*/}
      {/*        </Radio.Group>*/}
      {/*      ) : (*/}
      {/*        <div>{i18n("SampleFilesListItem.no-files")}</div>*/}
      {/*      )*/}
      {/*    }*/}
      {/*  />*/}
      {/*</List.Item>*/}
    </div>
  );
}
