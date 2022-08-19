import { LabeledValue } from "antd/lib/select";
import {
  FullNcbiPlatforms,
  getNCBIPlatforms,
} from "../../../../apis/export/ncbi";
import { getFilesForSamples } from "../../../../apis/projects/samples";
import { getStoredSamples } from "../../../../utilities/session-utilities";

/**
 * Fetch the NCBI sequencer platforms from the server and format them
 * into LabelAndValue's for consumption by an Ant Design Cascader component
 */
export async function getNCBIPlatformsAsCascaderOptions(): Promise<
  LabeledValue[]
> {
  return getNCBIPlatforms().then((platforms: FullNcbiPlatforms) => {
    return Object.keys(platforms).map((platform) => ({
      value: platform,
      label: platform,
      children: platforms[platform].map((child: string) => ({
        value: child,
        label: child,
      })),
    }));
  });
}

/**
 * Hydrate samples stored in samples storage with both sequence file data,
 * and add the required fields for the export to the SRA.
 */
export async function hydrateStoredSamples() {
  const { samples, projectId } = await getStoredSamples();
  return getFilesForSamples({
    ids: samples.map((sample) => sample.id),
    projectId,
  }).then((files) => {
    return samples.reduce(
      (prev, sample, index) => ({
        ...prev,
        [sample.name]: {
          bioSample: "",
          instrumentModel: "",
          libraryConstructionProtocol: "",
          librarySelection: "",
          librarySource: "",
          libraryStrategy: "",
          key: sample.name,
          name: sample.name,
          id: sample.id,
          libraryName: sample.name,
          files: files[index],
        },
      }),
      {}
    );
  });
}
