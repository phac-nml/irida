import { Form, Select } from "antd";
import React from "react";
import styled from "styled-components";
import { compareRestrictionLevels } from "../../../../../utilities/restriction-utilities";

const FormItem = styled(Form.Item)`
  margin-bottom: 0;
`;

export function MetadataRestrictionSelect({
  restrictions,
  onChange,
  currentRestriction,
  targetRestrictions,
  fieldKey,
}) {
  const [value, setValue] = React.useState(() =>
    compareRestrictionLevels(
      currentRestriction,
      targetRestrictions[fieldKey]
    ) <= 0
      ? currentRestriction
      : targetRestrictions[fieldKey]
  );
  console.info({ value });
  const difference = compareRestrictionLevels(
    currentRestriction,
    targetRestrictions[currentRestriction]
  );

  const select = (
    <Select value={value} onChange={setValue}>
      {restrictions.map((restriction) => (
        <Select.Option key={restriction.value} value={restriction.value}>
          {restriction.label}
        </Select.Option>
      ))}
    </Select>
  );
  return (
    <FormItem hasFeedback validateStatus={difference > 0 ? "warning" : ""}>
      {select}
    </FormItem>
  );
}
