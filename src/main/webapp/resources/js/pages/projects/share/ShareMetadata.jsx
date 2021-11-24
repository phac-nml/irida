import { Table, Tag } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import {
  getMetadataRestrictions,
  useGetMetadataFieldsForProjectQuery,
} from "../../../apis/metadata/field";
import { getColourForRestriction } from "../../../utilities/restriction-utilities";
import { MetadataRestrictionSelect } from "../settings/components/metadata/MetadataRestrictionSelect";

export function ShareMetadata() {
  const [restrictions, setRestrictions] = React.useState([]);
  const [targetRestrictions, setTargetRestrictions] = React.useState(null);

  const {
    originalSamples,
    currentProject,
    locked,
    projectId,
    remove,
  } = useSelector((state) => state.shareReducer);
  const { data: fields } = useGetMetadataFieldsForProjectQuery(currentProject);
  const { data: targetFields } = useGetMetadataFieldsForProjectQuery(
    projectId,
    {
      skip: !projectId,
    }
  );

  console.log(targetFields);

  React.useEffect(() => {
    getMetadataRestrictions().then(setRestrictions);
  }, []);

  React.useEffect(() => {
    if (targetFields) {
      // Create a dictionary
      const restrictions = {};
      targetFields.forEach((restriction) => {
        restrictions[restriction.fieldKey] = restriction.restriction;
      });
      setTargetRestrictions(restrictions);
    }
  }, [targetFields]);

  const columns = [
    {
      title: "Field",
      key: "label",
      dataIndex: "label",
    },
    {
      title: "Current Restriction",
      key: "current",
      dataIndex: "restriction",
      render(text) {
        const label = restrictions.find((r) => r.value === text)?.label;
        if (label) {
          return <Tag color={getColourForRestriction(text)}>{label}</Tag>;
        }
        return text;
      },
    },
    {
      title: "Target Restriction",
      key: "target",
      dataIndex: "restriction",
      render(currentRestriction, item) {
        if (targetRestrictions) {
          return (
            <MetadataRestrictionSelect
              fieldKey={item.fieldKey}
              restrictions={restrictions}
              onChange={(value) => console.log(value)}
              currentRestriction={currentRestriction}
              targetRestrictions={targetRestrictions}
            />
          );
        }
        return "---";
      },
    },
  ];

  return <Table columns={columns} dataSource={fields} />;
}
