import React from "react";
import { Select, SelectProps, Tag, Typography } from "antd";
import { LabeledValue } from "antd/lib/select";

export type SelectListItem = { id: number; name: string };
export interface SelectListProps extends SelectProps {
  selectList: SelectListItem[];
}

/**
 * React component for displaying a drop-down menu.
 * @param selectList - list that is to be displayed
 * @param onChange - function that is called when select option has changed
 * @param placeholder - placeholder of select
 * @param defaultValue - identifier of the select list item that is to be displayed by default
 * @constructor
 */
export function SearchByNameAndIdSelect({
  selectList,
  onChange,
  placeholder,
  defaultValue = null,
}: SelectListProps): JSX.Element {
  const [options, setOptions] = React.useState<LabeledValue[]>(() =>
    formatOptions(selectList)
  );

  function formatOptions(values: SelectListItem[]) {
    if (!values) return [];
    return values.map((selectListItem) => ({
      label: (
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            width: "100%",
          }}
        >
          <Typography.Text ellipsis={{ tooltip: true }}>
            {selectListItem.name}
          </Typography.Text>
          <Tag>
            {i18n("SearchByNameAndIdSelect.label.id", selectListItem.id)}
          </Tag>
        </div>
      ),
      value: selectListItem.id,
    }));
  }

  React.useEffect(() => {
    setOptions(formatOptions(selectList));
  }, [selectList]);

  const handleSearch = (value: string) => {
    const lowerValue = value.toLowerCase();

    const available = selectList.filter(
      (selectItem: { name: string; id: { toString: () => string } }) =>
        selectItem.name.toLowerCase().includes(lowerValue) ||
        selectItem.id.toString() === value
    );
    const formatted = formatOptions(available);
    setOptions(formatted);
  };

  return (
    <Select
      className="t-project-select"
      autoFocus
      showSearch
      size="large"
      style={{ width: `100%` }}
      options={options}
      placeholder={placeholder}
      filterOption={false}
      onSearch={handleSearch}
      onChange={onChange}
      defaultValue={defaultValue}
    />
  );
}
