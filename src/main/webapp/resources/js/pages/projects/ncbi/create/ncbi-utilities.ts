import { FullNcbiPlatforms } from "../../../../apis/export/ncbi";
import { Option } from "../../../../types/ant-design";
import { StoredSample } from "../../../../utilities/session-utilities";
import { SampleRecord } from "./CreateNcbiExport";
import { fetchSampleFiles } from "../../../../apis/samples/samples";

export function formatStoredAsNcbiBiosample(
  prev: SampleRecord,
  { id, name }: StoredSample
): SampleRecord {
  fetchSampleFiles;

  return {
    ...prev,
    [name]: {
      key: name,
      name,
      id,
      library_name: name,
    },
  };
}

export function formatPlatformsAsCascaderOptions(
  platforms: FullNcbiPlatforms
): Option[] {
  return Object.keys(platforms).map((platform) => ({
    value: platform,
    label: platform,
    children: platforms[platform].map((child: string) => ({
      value: child,
      label: child,
    })),
  }));
}
