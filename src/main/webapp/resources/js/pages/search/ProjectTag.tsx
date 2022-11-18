import React from "react";
import { generateColourForItem } from "../../utilities/colour-utilities";
import { Tag } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";

const colourMap = new Map<number, { text: string; background: string }>();
export default function ProjectTag({
  project,
}: {
  project: { id: number; name: string };
}) {
  if (!(project.id in colourMap)) {
    colourMap.set(
      project.id,
      generateColourForItem({
        id: project.id,
        label: project.name,
      })
    );
  }

  const colour = colourMap.get(project.id);
  if (!colour) throw new Error("THIS IS JUST A TYPE CHECK COLOUR SHOULD EXIST");

  return (
    <Tag
      key={`project-tag-${project.id}`}
      color={colour.background}
      style={{ border: `1px solid ${colour.text}` }}
    >
      <a
        style={{ color: colour.text }}
        href={setBaseUrl(`/projects/${project.id}`)}
      >
        {project.name}
      </a>
    </Tag>
  );
}
