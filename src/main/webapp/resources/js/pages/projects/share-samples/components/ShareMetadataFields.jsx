import { Table } from "antd";
import React from "react";
import { blue6, green6 } from "../../../../styles/colors";
import { setBaseUrl } from "../../../../utilities/url-utilities";

export function ShareMetadataFields({ projectId }) {
  const [fields, setFields] = React.useState();

  const getMetadataFields = React.useCallback(async () => {
    const [currentResponse, targetResponse] = await Promise.all([
      fetch(setBaseUrl(`/ajax/metadata/fields?projectId=${projectId}`)),
      fetch(setBaseUrl(`/ajax/metadata/fields?projectId=${18}`)),
    ]);
    const currentFields = await currentResponse.json();
    const targetFields = await targetResponse.json();

    return currentFields.map((current) => {
      const target = targetFields.find(
        (element) => element.label === current.label
      );
      return {
        current,
        target:
          target !== undefined
            ? { ...target, exists: true }
            : { ...current, exists: false },
      };
    });
  }, [projectId]);

  React.useEffect(() => {
    getMetadataFields().then(setFields);
  }, [getMetadataFields]);

  return (
    <Table
      rowKey={(item) => `field-${item.current.id}`}
      columns={[
        { title: "Field Label", dataIndex: ["current", "label"] },
        {
          title: "Current Project Restrictions",
          dataIndex: ["current", "restriction"],
        },
        {
          title: "Target Project Restrictions",
          dataIndex: ["target", "restriction"],
          render: (text, item) => {
            console.log({ text, item });
            return (
              <div
                style={{ backgroundColor: item.target.exists ? blue6 : green6 }}
              >
                {text}
              </div>
            );
          },
        },
      ]}
      dataSource={fields}
    />
  );
}
