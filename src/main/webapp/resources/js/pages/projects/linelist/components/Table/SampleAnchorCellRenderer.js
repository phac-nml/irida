const { BASE_URL } = window.TL;

/**
 * Used to render the sample name as an anchor tag to the page.
 */
export class SampleAnchorCellRenderer {
  init(params) {
    const anchor = document.createElement("a");
    anchor.href = `${BASE_URL}samples/${Number(params.data.sampleId)}`;
    anchor.innerText = params.value;
    this.eGui = anchor;
  }

  getGui() {
    return this.eGui;
  }
}
