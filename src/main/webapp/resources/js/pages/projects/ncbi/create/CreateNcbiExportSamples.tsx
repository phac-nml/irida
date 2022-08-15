import { CheckOutlined, CloseOutlined } from "@ant-design/icons";
import { Avatar, Collapse, FormInstance } from "antd";
import { NamePath } from "antd/lib/form/interface";
import React from "react";
import { useLoaderData } from "react-router-dom";
import { LoaderValues, SampleRecord } from "./CreateNcbiExport";
import CreateNcbiSampleDetails from "./CreateNcbiSampleDetails";

/**
 * React component to display if a sample if valid for export to the NCBI.
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
 * @constructor
 */
function CreateNcbiExportSamples({
  form,
}: {
  form: FormInstance;
}): JSX.Element {
  const { samples }: LoaderValues = useLoaderData();
  const values = Object.values(samples);
  const [validationStatus, setValidationStatus] = React.useState<boolean[]>(
    () =>
      values.map(
        (sample) =>
          sample.files.singles.length > 0 || sample.files.pairs.length > 0
      )
  );

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

    fields.map((field) => {
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

  return (
    <Collapse accordion>
      {values.map((sample, index) => (
        <Collapse.Panel
          key={String(sample.key)}
          header={sample.name}
          extra={<SampleValidIcon status={validationStatus[index]} />}
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
