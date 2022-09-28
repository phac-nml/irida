import { Metadata } from "../../apis/analysis/analysis";

export = PHYLOCANVAS;
export as namespace PHYLOCANVAS;
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Shapes, TreeTypes } from "@phylocanvas/phylocanvas.gl";

declare namespace PHYLOCANVAS {
  interface TreeProperties {
    alignLabels: boolean;
    blocks: string[];
    blockLength: number;
    branchZoom: number;
    fontFamily: string;
    fontSize: number;
    interactive: boolean;
    metadata: Metadata;
    nodeShape: Shapes.Dot; // TODO: Do we want more options on right click or something?
    padding: number;
    showBlockHeaders: boolean;
    showLabels: boolean;
    showLeafLabels: boolean;
    stepZoom: number;
    source: string;
    type: TreeType;
    zoom: number;
  }

  interface MetadataColourMap {
    [key: string]: { [key: string]: string };
  }

  type Template = { id: number; label: string; fields?: string[] };

  type TreeType =
    | TreeTypes.Rectangle
    | TreeTypes.Radial
    | TreeTypes.Cirular
    | TreeTypes.Diagonal
    | TreeTypes.Hierarchical;
}
