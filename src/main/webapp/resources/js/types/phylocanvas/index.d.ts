export = PHYLOCANVAS;
export as namespace PHYLOCANVAS;
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
    type:
      | TreeTypes.Rectangle
      | TreeTypes.Radial
      | TreeTypes.Cirular
      | TreeTypes.Diagonal
      | TreeTypes.Hierarchical;
    zoom: number;
  }

  type Metadata = {
    [key: string]: { label: string; value: string };
  };

  interface MetadataColourMap {
    [key: string]: { [key: string]: string };
  }

  type Template = { id: number; label: string; fields?: string[] };
}
