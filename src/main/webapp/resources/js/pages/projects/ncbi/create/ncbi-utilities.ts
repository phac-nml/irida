import { LabeledValue } from "antd/lib/select";
import { FullNcbiPlatforms } from "../../../../apis/export/ncbi";

export function formatPlatformsAsCascaderOptions(
  platforms: FullNcbiPlatforms
): LabeledValue[] {
  return Object.keys(platforms).map((platform) => ({
    value: platform,
    label: platform,
    children: platforms[platform].map((child: string) => ({
      value: child,
      label: child,
    })),
  }));
}
