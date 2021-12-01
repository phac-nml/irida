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
  /**
   * Available restrictions for metadata fields
   */
  const [restrictions, setRestrictions] = React.useState([]);
  const [targetRestrictions, setTargetRestrictions] = React.useState(null);

  const { currentProject, projectId } = useSelector(
    (state) => state.shareReducer
  );

  /**
   * Get the fields for the current project.  The restrictions from these fields
   * will act as a base for the restriction level when the fields are shared.
   */
  const {
    data: fields,
    isLoading: loadingFields,
  } = useGetMetadataFieldsForProjectQuery(currentProject);

  /**
   * Target project metadata fields. Needed to determine which fields will be
   * on both projects so that the new restriction for a field should start at
   * the highest level of restriction.
   */
  const { data: targetFields } = useGetMetadataFieldsForProjectQuery(
    projectId,
    {
      skip: !projectId,
    }
  );

  /**
   * On load, get metadata restrictions that are possible for a project.
   * These are formatted for Select inputs ({label, value}).
   */
  React.useEffect(() => {
    getMetadataRestrictions().then(setRestrictions);
  }, []);

  const updateRestrictionForField = (fieldKey, level) =>
    setTargetRestrictions({
      ...targetRestrictions,
      [fieldKey]: level,
    });

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
              currentRestriction={currentRestriction}
              restriction={targetRestrictions[item.fieldKey]}
              onChange={updateRestrictionForField}
              restrictions={restrictions}
            />
          );
        }
        return "---";
      },
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={fields}
      scroll={{ y: 600 }}
      pagination={false}
    />
  );
}
