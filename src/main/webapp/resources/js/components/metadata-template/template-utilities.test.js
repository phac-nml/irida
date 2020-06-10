import { parseMetadataTemplateUrl } from "./template-utilities";

test("Should parse the metadata template default url", () => {
  const [url, id, path] = parseMetadataTemplateUrl(
    "http://irida.ca/projects/4/metadata-templates/2"
  );
  expect(url).toBe("/projects/4/metadata-templates/2");
  expect(id).toBe(2);
  expect(path).toBe("details");
});

test("Should parse the metadata template fields url", () => {
  const [url, id, path] = parseMetadataTemplateUrl(
    "http://irida.ca/projects/4/metadata-templates/2/fields"
  );
  expect(url).toBe("/projects/4/metadata-templates/2");
  expect(id).toBe(2);
  expect(path).toBe("fields");
});
