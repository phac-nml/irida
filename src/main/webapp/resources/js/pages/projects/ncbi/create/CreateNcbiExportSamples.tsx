import { CheckOutlined, CloseOutlined } from "@ant-design/icons";
import { Button, Collapse, FormInstance, Space, Tag } from "antd";
import { NamePath } from "antd/lib/form/interface";
import React from "react";
import { FormSample, SampleRecord } from "./CreateNcbiExport";
import CreateNcbiSampleDetails from "./CreateNcbiSampleDetails";

/**
 * React component to display if a sample is valid for export to the NCBI.
 * @param status
 * @constructor
 */
function SampleValidIcon({ status }: { status: boolean }) {
  return status ? (
    <Tag color="green" icon={<CheckOutlined />}>
      {"VALID"}
    </Tag>
  ) : (
    <Tag color="red" icon={<CloseOutlined />}>
      {"INVALID"}
    </Tag>
  );
}

/**
 * React component to render a sample to be exported to the NCBI SRA
 * @param form - export form
 * @param samples - object of samples to export
 * @param removeSample - method to remove a sample from the form
 * @constructor
 */
function CreateNcbiExportSamples({
  form,
  samples,
  removeSample,
}: {
  form: FormInstance;
  samples: Record<string, SampleRecord>;
  removeSample: (
    e: React.MouseEvent<HTMLElement>,
    sample: SampleRecord
  ) => void;
}): JSX.Element {
  const [validationStatus, setValidationStatus] = React.useState<boolean[]>([]);

  const checkStatus = (sample: SampleRecord, index: number): void => {
    const fields: Array<NamePath> = [
      ["samples", sample.name, "bioSample"],
      ["samples", sample.name, "libraryName"],
      ["samples", sample.name, "libraryStrategy"],
      ["samples", sample.name, "librarySource"],
      ["samples", sample.name, "libraryConstructionProtocol"],
      ["samples", sample.name, "instrumentModel"],
      ["samples", sample.name, "librarySelection"],
      ["samples", sample.name, "files", "singles"],
      ["samples", sample.name, "files", "pairs"],
    ];

    // const values = form.getFieldsValue(fields);
    //
    // // Make sure that each input has a value
    // const validCount = Object.values(values.samples[sample.name]).filter(
    //   (value) => value.length === 0
    // );

    // const touched: Array<NamePath> = [];
    // let hasUntouched = false;
    //
    // fields.forEach((field) => {
    //   if (form.getFieldError(field)) {
    //     console.log(form.getFieldError(field));
    //     touched.push(field);
    //   } else {
    //     if (form.getFieldValue(field) === undefined) {
    //       hasUntouched = true;
    //     }
    //   }
    // });
    //
    // if (hasUntouched) {
    //   // If it has untouched fields, then it is not valid yet!
    //   const updated = [...validationStatus];
    //   updated[index] = false;
    //   setValidationStatus(updated);
    // } else {
    //   form
    //     .validateFields(touched)
    //     .then(() => {
    //       const updated = [...validationStatus];
    //       updated[index] = true;
    //       setValidationStatus(updated);
    //     })
    //     .catch(() => {
    //       const updated = [...validationStatus];
    //       updated[index] = false;
    //       setValidationStatus(updated);
    //     });
    // }
  };

  return (
    <Collapse accordion>
      {Object.values(samples).map((sample, index) => (
        <Collapse.Panel
          className="t-sample-panel"
          key={String(sample.key)}
          header={
            <Space>
              <SampleValidIcon status={validationStatus[index]} />
              <span className="t-sample-name">{sample.name}</span>
            </Space>
          }
          extra={
            <Button
              size="small"
              onClick={(event) => removeSample(event, sample)}
            >
              {i18n("CreateNcbiExport.remove")}
            </Button>
          }
        >
          <CreateNcbiSampleDetails
            sample={sample}
            onChange={() => checkStatus(sample, index)}
          />
        </Collapse.Panel>
      ))}
    </Collapse>
  );
}

export default CreateNcbiExportSamples;
