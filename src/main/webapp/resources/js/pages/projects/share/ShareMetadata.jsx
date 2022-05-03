import { Alert, Table, Tag } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  getMetadataRestrictions,
  useGetMetadataFieldsForProjectQuery,
} from "../../../apis/metadata/field";
import {
  compareRestrictionLevels,
  getColourForRestriction,
} from "../../../utilities/restriction-utilities";
import { setMetadataRestrictions } from "./shareSlice";
import { TargetMetadataRestriction } from "../../../components/metadata/TargetMetadataRestriction";

/**
 * React component to display metadata restrictions.
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareMetadata() {
  const dispatch = useDispatch();
  /**
   * Available restrictions for metadata fields
   */
  const [restrictions, setRestrictions] = React.useState([]);

  const { currentProject, targetProject, metadataRestrictions } = useSelector(
    (state) => state.shareReducer
  );

  /**
   * Get the fields for the current project.  The restrictions from these fields
   * will act as a base for the restriction level when the fields are shared.
   */
  const { data: sourceFields } = useGetMetadataFieldsForProjectQuery(
    currentProject
  );

  /**
   * Target project metadata fields. Needed to determine, which fields will be
   * on both projects so that the new restriction for a field should start at
   * the highest level of restriction.
   */
  const { data: targetExistingFields } = useGetMetadataFieldsForProjectQuery(
    targetProject.identifier,
    {
      skip: !targetProject || metadataRestrictions.length === 0,
    }
  );

  React.useEffect(() => {
    if (sourceFields && targetExistingFields) {
      const existing = targetExistingFields.reduce(
        (fields, field) => ({
          ...fields,
          [field.fieldKey]: field,
        }),
        {}
      );
      const fields = sourceFields.map((field) => {
        const newField = { ...field, current: field.restriction };
        delete newField.initial;
        if (existing[newField.fieldKey]) {
          /*
          If field exists in target project
           */
          newField.target = existing[newField.fieldKey].restriction;
          newField.difference = compareRestrictionLevels(
            newField.restriction,
            newField.target
          );

          newField.restriction =
            newField.difference < 0 ? newField.current : newField.target;
        }
        return newField;
      });
      dispatch(setMetadataRestrictions(fields));
    } else if (sourceFields) {
      // Allow user to see what the restrictions are on the source fields
      dispatch(setMetadataRestrictions(sourceFields));
    }
  }, [dispatch, sourceFields, targetExistingFields]);

  /**
   * On load, get metadata restrictions that are possible for a project.
   * These are formatted for Select inputs ({label, value}).
   */
  React.useEffect(() => {
    getMetadataRestrictions().then(setRestrictions);
  }, []);

  const columns = [
    {
      title: i18n("ShareMetadata.field"),
      key: "label",
      dataIndex: "label",
      render: (label, field) => <span className="t-field-label">{label}</span>,
    },
    {
      title: i18n("ShareMetadata.current"),
      key: "current",
      dataIndex: "current",
      render(text, item, index) {
        const field = restrictions.find(
          (restriction) => restriction.value === sourceFields[index].restriction
        );
        if (field) {
          return (
            <Tag color={getColourForRestriction(field.value)}>
              {field.label}
            </Tag>
          );
        }
        return text;
      },
    },
    {
      title: i18n("ShareMetadata.target"),
      key: "target",
      dataIndex: "restriction",
      render(currentRestriction, item) {
        if (targetExistingFields !== undefined) {
          return (
            <TargetMetadataRestriction
              field={item}
              restrictions={restrictions}
            />
          );
        }
        return "---";
      },
    },
    {
      title: "",
      key: "new",
      dataIndex: "fieldKey",
      width: 100,
      render(restriction, item) {
        if (targetExistingFields === undefined || item.target) return undefined;
        return <Tag>{i18n("ShareMetadata.newField")}</Tag>;
      },
    },
  ];

  return metadataRestrictions.length ? (
    <Table
      className="t-meta-table"
      columns={columns}
      dataSource={metadataRestrictions}
    />
  ) : (
    <Alert
      message={i18n("ShareMetadata.none.message")}
      description={i18n("ShareMetadata.none.description")}
      type="info"
      showIcon
    />
  );
}
