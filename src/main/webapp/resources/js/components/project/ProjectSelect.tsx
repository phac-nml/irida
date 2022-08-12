import React from "react";
import { Select, SelectProps, Tag, Typography } from "antd";

export type Project = { identifier: number; name: string };

export interface ProjectSelectProps extends SelectProps {
  projects: Project[];
}

/**
 * React component for displaying a project drop-down menu.
 * @param projects - list of projects that is to be displayed
 * @param onChange - function that is called when select option has changed
 * @param defaultValue - project identifier of the project that is to be displayed by default
 * @constructor
 */
export function ProjectSelect({
  projects,
  onChange = undefined,
  defaultValue = null,
}: ProjectSelectProps): JSX.Element {
  const [options, setOptions] = React.useState(() => formatOptions(projects));

  function formatOptions(values: Project[]) {
    if (!values) return [];
    return values.map((project) => ({
      label: (
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            width: "100%",
          }}
        >
          <Typography.Text ellipsis={{ tooltip: true }}>
            {project.name}
          </Typography.Text>
          <Tag>{i18n("ProjectSelect.label.id", project.identifier)}</Tag>
        </div>
      ),
      value: project.identifier,
      selected: project.name,
    }));
  }

  React.useEffect(() => {
    setOptions(formatOptions(projects));
  }, [projects]);

  const handleSearch = (value: string) => {
    const lowerValue = value.toLowerCase();

    const available = projects.filter(
      (project) =>
        project.name.toLowerCase().includes(lowerValue) ||
        project.identifier.toString() === value
    );
    const formatted = formatOptions(available);
    setOptions(formatted);
  };

  return (
    <Select
      optionLabelProp="selected"
      autoFocus
      showSearch
      size="large"
      style={{ width: `100%` }}
      options={options}
      className="t-project-select"
      filterOption={false}
      onSearch={handleSearch}
      onChange={onChange}
      defaultValue={defaultValue}
    />
  );
}
