import { Metadata } from "../../apis/analysis/analysis";

export = PHYLOCANVAS;
export as namespace PHYLOCANVAS;
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Shapes } from "@phylocanvas/phylocanvas.gl";

declare namespace PHYLOCANVAS {
  interface PhyloCanvas {
    exportNewick: () => BlobPart;
    exportSVG: () => Blob;
    exportPNG: () => string;
    destroy: () => void;
    setProps: (props: Partial<TreeProperties>) => void;
    size: {
      height: number;
      width: number;
    };
  }

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
    size: Size;
    source: string;
    type: TreeType;
    zoom: number;
  }

  interface MetadataColourMap {
    [key: string]: { [key: string]: string };
  }

  type Size = { height: number; width: number };

  type Template = { id: number; label: string; fields?: string[] };

  type TreeType = "rc" | "rd" | "cr" | "dg" | "hr";
}
