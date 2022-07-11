import { FullNcbiPlatforms } from "../../../../apis/export/ncbi";
import { Option } from "../../../../types/ant-design";
import { StoredSample } from "../../../../utilities/session-utilities";
import { SampleRecords } from "./CreateNcbiExport";

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
