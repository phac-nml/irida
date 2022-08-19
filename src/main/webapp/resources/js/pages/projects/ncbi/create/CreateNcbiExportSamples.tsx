import { CheckOutlined, CloseOutlined } from "@ant-design/icons";
import { Avatar, Button, Collapse, FormInstance, Space } from "antd";
import { NamePath } from "antd/lib/form/interface";
import React from "react";
import { SampleRecord } from "./CreateNcbiExport";
import CreateNcbiSampleDetails from "./CreateNcbiSampleDetails";

/**
 * React component to display if a sample is valid for export to the NCBI.
 * @param status
 * @constructor
 */
function SampleValidIcon({ status }: { status: boolean }) {
  return status ? (
    <Avatar
      icon={<CheckOutlined />}
      size="small"
      style={{
        backgroundColor: `var(--primary-green)`,
        color: `var(--grey-1)`,
      }}
    />
  ) : (
    <Avatar
      icon={<CloseOutlined />}
      size="small"
      style={{
        backgroundColor: `var(--primary-red)`,
        color: `var(--grey-1)`,
      }}
    />
  );
}

/**
 * React component to render a sample to be exported to the NCBI SRA
 * @param form
 * @param samples
 * @constructor
 */
function CreateNcbiExportSamples({
  form,
  samples,
}: {
  form: FormInstance;
  samples: SampleRecord[];
}): JSX.Element {
  const [sampleRecords, setSampleRecords] = React.useState(samples);
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

    const touched: Array<NamePath> = [];
    let hasUntouched = false;

    fields.forEach((field) => {
      if (form.isFieldTouched(field)) {
        touched.push(field);
      } else {
        if (form.getFieldValue(field) === undefined) {
          hasUntouched = true;
        }
      }
    });

    if (hasUntouched) {
      // If it has untouched fields, then it is not valid yet!
      const updated = [...validationStatus];
      updated[index] = false;
      setValidationStatus(updated);
    } else {
      form
        .validateFields(touched)
        .then(() => {
          const updated = [...validationStatus];
          updated[index] = true;
          setValidationStatus(updated);
        })
        .catch(() => {
          const updated = [...validationStatus];
          updated[index] = false;
          setValidationStatus(updated);
        });
    }
  };

  /**
   * Remove a sample from the export.
   * Note: this only removes from the export, but if the page is reloaded, they still
   * exist in the sessionStorage.
   * @param event
   * @param sample - sample to remove
   */
  function removeSample(
    event: React.MouseEvent<HTMLElement>,
    sample: SampleRecord
  ) {
    event.stopPropagation();
    const updatedValues = [...sampleRecords];
    const start = updatedValues.findIndex((s) => s.name === sample.name);
    updatedValues.splice(start, 1);
    setSampleRecords(updatedValues);
  }

  return (
    <Collapse accordion>
      {sampleRecords.map((sample, index) => (
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
