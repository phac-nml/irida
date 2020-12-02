import React from "react";
import { setBaseUrl } from "../../../utilities/url-utilities";

export function SampleFiles({ id }) {
  React.useEffect(() => {
    fetch(setBaseUrl(`/ajax/samples/${id}/files`))
      .then((response) => response.json())
      .then(({ assemblies, paired, singles, fast5 }) => {});
  }, []);

  return <p>files</p>;
}
