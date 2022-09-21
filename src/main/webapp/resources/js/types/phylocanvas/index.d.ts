export = PHYLOCANVAS;
export as namespace PHYLOCANVAS;
import { Shapes, TreeTypes } from "@phylocanvas/phylocanvas.gl";

declare namespace PHYLOCANVAS {
  type TreeProperties = {
    alignLabels: boolean;
    blocks: string[];
    blockLength: number;
    branchZoom: number;
    fontFamily: string;
    fontSize: number;
    interactive: boolean;
    metadata: {
      [key: string]: { label: string; value: string };
    };
    nodeShape: Shapes.Dot; // TODO: Do we want more options on right click or something?
    padding: number;
    showBlockHeaders: boolean;
    showLabels: boolean;
    showLeafLabels: boolean;
    stepZoom: number;
    type:
      | TreeTypes.Rectangle
      | TreeTypes.Radial
      | TreeTypes.Cirular
      | TreeTypes.Diagonal
      | TreeTypes.Hierarchical;
    zoom: number;
  };

  type MetadataColourMap = { [key: string]: { [key: string]: string } };
}