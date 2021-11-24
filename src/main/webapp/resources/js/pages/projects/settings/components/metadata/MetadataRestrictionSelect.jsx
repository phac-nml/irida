import { Form, Select } from "antd";
import React from "react";
import styled from "styled-components";
import { compareRestrictionLevels } from "../../../../../utilities/restriction-utilities";

const FormItem = styled(Form.Item)`
  margin-bottom: 0;
`;

export function MetadataRestrictionSelect({
  fieldKey,
  currentRestriction,
  restriction,
  restrictions = [],
  onChange,
}) {
  const [feedback, setFeedback] = React.useState({
    hasFeedback: false,
    validateStatus: "",
  });

  React.useEffect(() => {
    const difference = compareRestrictionLevels(
      currentRestriction,
      restriction
    );
    setFeedback({
      hasFeedback: true,
      validateStatus: difference >= 0 ? "success" : "warning",
    });
  }, [currentRestriction, restriction]);

  return (
    <FormItem {...feedback}>
      <Select
        value={restriction}
        onChange={(value) => onChange(fieldKey, value)}
      >
        {restrictions.map(({ label, value }) => (
          <Select.Option key={value} value={value}>
            {label}
          </Select.Option>
        ))}
      </Select>
    </FormItem>
  );
}
