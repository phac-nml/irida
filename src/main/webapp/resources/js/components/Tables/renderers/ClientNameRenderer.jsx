import React from "react";

export function ClientNameRenderer({ data }) {
  return (
    <a href={`clients/${data.id}`}>
      {data.name}
    </a>
  );
}
